import crypto from 'node:crypto';
import * as client from 'openid-client';
import { env } from '~/.server/lib/env';
import { logger } from '~/.server/lib/logger';
import { safeRedirectPath, toExternalUrl } from '~/.server/lib/redirect';
import { type AsyncResult, err, isErr, ok } from '~/.server/lib/result';
import {
    type AuthPayload,
    type AuthTransaction,
    clearSidCookie,
    clearSidTxnCookie,
    createAuthTransaction,
    createSidTxnCookie,
    deleteAuthTransaction,
    deleteSession,
    getAuthTransaction,
    getSessionEntryFromRequest,
    getSidTxnFromRequest,
    type SessionPayload,
    type SessionUser,
    updateSession,
} from '~/.server/service/session';

const FALLBACK_ACCESS_TOKEN_LIFETIME_MS = 60 * 60 * 1000;
const OIDC_SCOPE = 'openid profile email';

let oidcConfigPromise: Promise<client.Configuration> | null = null;

async function getOidcConfig(): Promise<client.Configuration> {
    if (!oidcConfigPromise) {
        oidcConfigPromise = client.discovery(
            new URL(env.get('KEYCLOAK_ISSUER')),
            env.get('KEYCLOAK_CLIENT_ID'),
            {
                client_secret: env.get('KEYCLOAK_CLIENT_SECRET'),
                token_endpoint_auth_method: 'client_secret_post',
            },
            client.ClientSecretPost(env.get('KEYCLOAK_CLIENT_SECRET')),
        );
    }

    return oidcConfigPromise;
}

function buildCallbackUrl(): string {
    return `${env.get('APP_BASE_URL')}/auth/callback`;
}

function buildPostLogoutRedirectUrl(): string {
    return `${env.get('APP_BASE_URL')}/`;
}

function buildLoginRedirectResponse(request: Request): Response {
    const url = new URL(request.url);
    const redirectTo = `${url.pathname}${url.search}`;
    const loginUrl = new URL('/login', url);
    loginUrl.searchParams.set('redirectTo', redirectTo);

    return new Response(null, {
        headers: { Location: loginUrl.toString() },
        status: 302,
    });
}

function getStringArrayClaim(value: unknown): readonly string[] {
    if (!Array.isArray(value)) {
        return [];
    }

    return value.filter((item): item is string => typeof item === 'string');
}

function getStringClaim(value: unknown): string | null {
    return typeof value === 'string' ? value : null;
}

async function mapUserInfo(accessToken: string, expectedSubject: string): Promise<SessionUser> {
    const config = await getOidcConfig();
    const userInfo = await client.fetchUserInfo(config, accessToken, expectedSubject);
    const claims = userInfo as Record<string, unknown>;

    return {
        email: getStringClaim(userInfo.email),
        name: getStringClaim(userInfo.name),
        preferredUsername: getStringClaim(userInfo.preferred_username),
        roles: getStringArrayClaim(claims.realm_roles),
        sub: getStringClaim(userInfo.sub) ?? '',
    };
}

async function createAuthPayloadFromTokenSet(
    tokenSet: Pick<client.TokenEndpointResponse, 'access_token' | 'expires_in' | 'id_token' | 'refresh_token'>,
    fallbackRefreshToken: string | undefined,
    fallbackIdToken: string | undefined,
    expectedSubject: string,
): AsyncResult<AuthPayload> {
    if (!tokenSet.access_token) {
        return err(new Error('No access token received'));
    }

    try {
        return ok({
            accessToken: tokenSet.access_token,
            expiresAt: tokenSet.expires_in
                ? Date.now() + tokenSet.expires_in * 1000
                : Date.now() + FALLBACK_ACCESS_TOKEN_LIFETIME_MS,
            idToken: tokenSet.id_token ?? fallbackIdToken,
            refreshToken: tokenSet.refresh_token ?? fallbackRefreshToken,
            user: await mapUserInfo(tokenSet.access_token, expectedSubject),
        });
    } catch (error) {
        return err(error instanceof Error ? error : new Error(String(error)));
    }
}

export async function requireAuthInLoader(request: Request): AsyncResult<SessionPayload, Response> {
    const entryResult = await getSessionEntryFromRequest(request);
    if (isErr(entryResult)) {
        logger.error('Failed to load session for auth check', { error: entryResult.error });
        return err(buildLoginRedirectResponse(request));
    }

    const entry = entryResult.value;
    if (!entry) {
        return err(buildLoginRedirectResponse(request));
    }

    if (entry.session.expiresAt < Date.now()) {
        if (!entry.session.refreshToken) {
            const deleteResult = await deleteSession(entry.id);
            if (isErr(deleteResult)) {
                logger.error('Failed to delete expired session', { error: deleteResult.error });
            }

            return err(buildLoginRedirectResponse(request));
        }

        const refreshed = await refreshAccessToken(entry.id, entry.session);
        if (!refreshed) {
            return err(buildLoginRedirectResponse(request));
        }

        return ok(refreshed);
    }

    return ok(entry.session);
}

export async function getAccessToken(request: Request): Promise<string | null> {
    const entryResult = await getSessionEntryFromRequest(request);
    if (isErr(entryResult)) {
        logger.error('Failed to load session for access token', { error: entryResult.error });
        return null;
    }

    const entry = entryResult.value;
    if (!entry) {
        return null;
    }

    if (entry.session.expiresAt > Date.now()) {
        return entry.session.accessToken;
    }

    if (!entry.session.refreshToken) {
        const deleteResult = await deleteSession(entry.id);
        if (isErr(deleteResult)) {
            logger.error('Failed to delete expired session', { error: deleteResult.error });
        }

        return null;
    }

    const refreshed = await refreshAccessToken(entry.id, entry.session);
    return refreshed?.accessToken ?? null;
}

export async function login(
    redirectTo?: string | null,
): AsyncResult<{ readonly sidTxnCookie: string; readonly url: string }> {
    const config = await getOidcConfig();
    const codeVerifier = client.randomPKCECodeVerifier();
    const codeChallenge = await client.calculatePKCECodeChallenge(codeVerifier);
    const state = crypto.randomUUID();

    const txnResult = await createAuthTransaction({
        codeVerifier,
        redirectTo: safeRedirectPath(redirectTo ?? null),
        state,
    });
    if (isErr(txnResult)) {
        return txnResult;
    }

    const authUrl = client.buildAuthorizationUrl(config, {
        code_challenge: codeChallenge,
        code_challenge_method: 'S256',
        redirect_uri: buildCallbackUrl(),
        scope: OIDC_SCOPE,
        state,
    });

    return ok({
        sidTxnCookie: createSidTxnCookie(txnResult.value),
        url: authUrl.toString(),
    });
}

export async function exchangeCodeForAuthPayload(
    currentUrl: URL,
    transaction: AuthTransaction,
): AsyncResult<AuthPayload> {
    try {
        const config = await getOidcConfig();
        const tokenSet = await client.authorizationCodeGrant(config, currentUrl, {
            expectedState: transaction.state,
            pkceCodeVerifier: transaction.codeVerifier,
        });

        if (!tokenSet.access_token) {
            return err(new Error('No access token received'));
        }

        const expectedSubject = tokenSet.claims()?.sub;
        if (!expectedSubject) {
            return err(new Error('No subject in ID token for UserInfo response validation'));
        }

        return createAuthPayloadFromTokenSet(tokenSet, tokenSet.refresh_token, tokenSet.id_token, expectedSubject);
    } catch (error) {
        return err(error instanceof Error ? error : new Error(String(error)));
    }
}

export async function loadAuthTransactionFromRequest(request: Request): AsyncResult<AuthTransaction | null> {
    const txnId = getSidTxnFromRequest(request);
    if (!txnId) {
        return ok(null);
    }

    const transactionResult = await getAuthTransaction(txnId);
    if (isErr(transactionResult)) {
        return transactionResult;
    }

    if (!transactionResult.value) {
        return ok(null);
    }

    const deleteResult = await deleteAuthTransaction(txnId);
    if (isErr(deleteResult)) {
        return deleteResult;
    }

    return ok(transactionResult.value);
}

export async function logout(session: SessionPayload | null): Promise<{
    readonly sidCookie: string;
    readonly sidTxnCookie: string;
    readonly url: string;
}> {
    if (session?.refreshToken) {
        try {
            const config = await getOidcConfig();
            await client.tokenRevocation(config, session.refreshToken, {
                token_type_hint: 'refresh_token',
            });
        } catch (error) {
            logger.warn('Failed to revoke refresh token', { error });
        }
    }

    const logoutUrl = new URL(`${env.get('KEYCLOAK_ISSUER')}/protocol/openid-connect/logout`);
    logoutUrl.searchParams.set('client_id', env.get('KEYCLOAK_CLIENT_ID'));
    logoutUrl.searchParams.set('post_logout_redirect_uri', buildPostLogoutRedirectUrl());

    if (session?.idToken) {
        logoutUrl.searchParams.set('id_token_hint', session.idToken);
    }

    return {
        sidCookie: clearSidCookie(),
        sidTxnCookie: clearSidTxnCookie(),
        url: logoutUrl.toString(),
    };
}

export function buildCallbackRequestUrl(request: Request): URL {
    return toExternalUrl(request);
}

async function refreshAccessToken(sessionId: string, session: SessionPayload): Promise<SessionPayload | null> {
    try {
        if (!session.refreshToken) {
            return null;
        }

        const config = await getOidcConfig();
        const tokenSet = await client.refreshTokenGrant(config, session.refreshToken);

        if (!tokenSet.access_token) {
            return null;
        }

        const expectedSubject = tokenSet.claims()?.sub ?? session.user.sub;
        if (!expectedSubject) {
            return null;
        }

        const updatedSessionResult = await createAuthPayloadFromTokenSet(
            tokenSet,
            tokenSet.refresh_token ?? session.refreshToken,
            tokenSet.id_token ?? session.idToken,
            expectedSubject,
        );
        if (isErr(updatedSessionResult)) {
            logger.error('Failed to build refreshed session payload', { error: updatedSessionResult.error });
            return null;
        }

        const updateResult = await updateSession(sessionId, updatedSessionResult.value);
        if (isErr(updateResult)) {
            logger.error('Failed to persist refreshed session', { error: updateResult.error });
            return null;
        }

        return updatedSessionResult.value;
    } catch (error) {
        logger.error('Failed to refresh access token', { error });
        const deleteResult = await deleteSession(sessionId);
        if (isErr(deleteResult)) {
            logger.error('Failed to delete session after refresh failure', { error: deleteResult.error });
        }

        return null;
    }
}
