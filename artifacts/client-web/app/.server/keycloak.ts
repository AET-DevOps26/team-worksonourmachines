import { createHash, randomBytes } from 'node:crypto';
import { createCookieSessionStorage, redirect, redirectDocument } from 'react-router';

const keycloakIssuerPublic = stripTrailingSlash(
    process.env.KEYCLOAK_ISSUER_PUBLIC ?? 'http://localhost:8080/realms/tutormatch',
);
const keycloakIssuerInternal = stripTrailingSlash(process.env.KEYCLOAK_ISSUER_INTERNAL ?? keycloakIssuerPublic);
const keycloakClientId = process.env.KEYCLOAK_CLIENT_ID ?? 'client-web';
const keycloakClientSecret = process.env.KEYCLOAK_CLIENT_SECRET ?? 'client-web-dev-secret';
const appBaseUrl = stripTrailingSlash(process.env.APP_BASE_URL ?? 'http://localhost:5173');
const callbackPath = '/auth/keycloak/callback';
const sessionSecret = process.env.SESSION_SECRET ?? 'dev-session-secret-change-before-production';

export type AuthenticatedUser = {
    sub: string;
    username: string;
    email?: string;
    name?: string;
    roles: string[];
};

type KeycloakSession = {
    authState?: string;
    codeVerifier?: string;
    idToken?: string;
    user?: AuthenticatedUser;
};

type TokenResponse = {
    access_token: string;
    id_token?: string;
    expires_in?: number;
    token_type: string;
};

type UserInfoResponse = {
    sub: string;
    preferred_username?: string;
    email?: string;
    name?: string;
};

const sessionStorage = createCookieSessionStorage<KeycloakSession>({
    cookie: {
        httpOnly: true,
        maxAge: 60 * 60,
        name: '__tutormatch_session',
        path: '/',
        sameSite: 'lax',
        secrets: [sessionSecret],
        secure: process.env.NODE_ENV === 'production',
    },
});

export async function getAuthenticatedUser(request: Request): Promise<AuthenticatedUser | null> {
    const session = await sessionStorage.getSession(request.headers.get('Cookie'));
    return session.get('user') ?? null;
}

export async function startKeycloakLogin(request: Request): Promise<Response> {
    const session = await sessionStorage.getSession(request.headers.get('Cookie'));
    const state = randomToken();
    const codeVerifier = randomToken();
    const codeChallenge = createCodeChallenge(codeVerifier);

    session.set('authState', state);
    session.set('codeVerifier', codeVerifier);

    const authorizationUrl = new URL(`${keycloakIssuerPublic}/protocol/openid-connect/auth`);
    authorizationUrl.searchParams.set('client_id', keycloakClientId);
    authorizationUrl.searchParams.set('redirect_uri', callbackUrl());
    authorizationUrl.searchParams.set('response_type', 'code');
    authorizationUrl.searchParams.set('scope', 'openid profile email');
    authorizationUrl.searchParams.set('state', state);
    authorizationUrl.searchParams.set('code_challenge', codeChallenge);
    authorizationUrl.searchParams.set('code_challenge_method', 'S256');

    return redirectDocument(authorizationUrl.toString(), {
        headers: {
            'Set-Cookie': await sessionStorage.commitSession(session),
        },
    });
}

export async function completeKeycloakLogin(request: Request): Promise<Response> {
    const requestUrl = new URL(request.url);
    const code = requestUrl.searchParams.get('code');
    const returnedState = requestUrl.searchParams.get('state');
    const error = requestUrl.searchParams.get('error');

    if (error) {
        return redirectWithError(`Keycloak rejected the login request: ${error}`);
    }

    const session = await sessionStorage.getSession(request.headers.get('Cookie'));
    const expectedState = session.get('authState');
    const codeVerifier = session.get('codeVerifier');

    if (!code || !returnedState || !expectedState || returnedState !== expectedState || !codeVerifier) {
        return redirectWithError('The Keycloak login response did not match the active browser session.');
    }

    const tokenResponse = await exchangeCodeForToken(code, codeVerifier);
    const userInfo = await fetchUserInfo(tokenResponse.access_token);
    const tokenClaims = decodeJwtPayload(tokenResponse.access_token);

    session.unset('authState');
    session.unset('codeVerifier');

    if (tokenResponse.id_token) {
        session.set('idToken', tokenResponse.id_token);
    }

    session.set('user', {
        email: userInfo.email,
        name: userInfo.name,
        roles: extractRealmRoles(tokenClaims),
        sub: userInfo.sub,
        username: userInfo.preferred_username ?? tokenClaims.preferred_username ?? userInfo.email ?? userInfo.sub,
    });

    return redirect('/', {
        headers: {
            'Set-Cookie': await sessionStorage.commitSession(session),
        },
    });
}

export async function endKeycloakSession(request: Request): Promise<Response> {
    const session = await sessionStorage.getSession(request.headers.get('Cookie'));
    const logoutUrl = new URL(`${keycloakIssuerPublic}/protocol/openid-connect/logout`);
    const idToken = session.get('idToken');

    logoutUrl.searchParams.set('client_id', keycloakClientId);
    logoutUrl.searchParams.set('post_logout_redirect_uri', `${appBaseUrl}/`);

    if (idToken) {
        logoutUrl.searchParams.set('id_token_hint', idToken);
    }

    return redirectDocument(logoutUrl.toString(), {
        headers: {
            'Set-Cookie': await sessionStorage.destroySession(session),
        },
    });
}

function callbackUrl(): string {
    return `${appBaseUrl}${callbackPath}`;
}

async function exchangeCodeForToken(code: string, codeVerifier: string): Promise<TokenResponse> {
    const response = await fetch(`${keycloakIssuerInternal}/protocol/openid-connect/token`, {
        body: new URLSearchParams({
            client_id: keycloakClientId,
            client_secret: keycloakClientSecret,
            code,
            code_verifier: codeVerifier,
            grant_type: 'authorization_code',
            redirect_uri: callbackUrl(),
        }),
        headers: {
            'content-type': 'application/x-www-form-urlencoded',
        },
        method: 'POST',
    });

    if (!response.ok) {
        throw new Response(await response.text(), {
            status: response.status,
            statusText: 'Keycloak token exchange failed',
        });
    }

    return response.json() as Promise<TokenResponse>;
}

async function fetchUserInfo(accessToken: string): Promise<UserInfoResponse> {
    const response = await fetch(`${keycloakIssuerInternal}/protocol/openid-connect/userinfo`, {
        headers: {
            authorization: `Bearer ${accessToken}`,
        },
    });

    if (!response.ok) {
        throw new Response(await response.text(), {
            status: response.status,
            statusText: 'Keycloak userinfo request failed',
        });
    }

    return response.json() as Promise<UserInfoResponse>;
}

function createCodeChallenge(codeVerifier: string): string {
    return createHash('sha256').update(codeVerifier).digest('base64url');
}

function decodeJwtPayload(token: string): Record<string, unknown> {
    const payload = token.split('.')[1];

    if (!payload) {
        return {};
    }

    return JSON.parse(Buffer.from(payload, 'base64url').toString('utf8')) as Record<string, unknown>;
}

function extractRealmRoles(claims: Record<string, unknown>): string[] {
    const realmAccess = claims.realm_access;

    if (!isRecord(realmAccess) || !Array.isArray(realmAccess.roles)) {
        return [];
    }

    return realmAccess.roles.filter((role): role is string => typeof role === 'string');
}

function randomToken(): string {
    return randomBytes(32).toString('base64url');
}

function redirectWithError(message: string): Response {
    const redirectUrl = new URL('/', appBaseUrl);
    redirectUrl.searchParams.set('auth_error', message);
    return redirect(redirectUrl.pathname + redirectUrl.search);
}

function stripTrailingSlash(value: string): string {
    return value.endsWith('/') ? value.slice(0, -1) : value;
}

function isRecord(value: unknown): value is Record<string, unknown> {
    return typeof value === 'object' && value !== null;
}
