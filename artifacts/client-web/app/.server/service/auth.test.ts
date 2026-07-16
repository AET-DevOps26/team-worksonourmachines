import { beforeEach, describe, expect, it, vi } from 'vitest';
import { err, ok } from '~/.server/lib/result';
import { sampleSession } from '~/.server/test/fixtures';

const createAuthTransaction = vi.fn();
const createSidTxnCookie = vi.fn((txnId: string) => `sid_txn=${txnId}`);
const clearSidCookie = vi.fn(() => 'sid=; Max-Age=0');
const clearSidTxnCookie = vi.fn(() => 'sid_txn=; Max-Age=0');
const deleteAuthTransaction = vi.fn();
const deleteSession = vi.fn();
const getAuthTransaction = vi.fn();
const getSessionEntryFromRequest = vi.fn();
const getSidTxnFromRequest = vi.fn();
const updateSession = vi.fn();

const discovery = vi.fn();
const buildAuthorizationUrl = vi.fn();
const calculatePKCECodeChallenge = vi.fn();
const randomPKCECodeVerifier = vi.fn();
const authorizationCodeGrant = vi.fn();
const fetchUserInfo = vi.fn();
const tokenRevocation = vi.fn();
const refreshTokenGrant = vi.fn();
const ClientSecretPost = vi.fn(() => ({}));

vi.mock('openid-client', () => ({
    authorizationCodeGrant,
    buildAuthorizationUrl,
    ClientSecretPost,
    calculatePKCECodeChallenge,
    discovery,
    fetchUserInfo,
    randomPKCECodeVerifier,
    refreshTokenGrant,
    tokenRevocation,
}));

vi.mock('~/.server/service/session', () => ({
    clearSidCookie,
    clearSidTxnCookie,
    createAuthTransaction,
    createSidTxnCookie,
    deleteAuthTransaction,
    deleteSession,
    getAuthTransaction,
    getSessionEntryFromRequest,
    getSidTxnFromRequest,
    updateSession,
}));

vi.mock('~/.server/lib/logger', () => ({
    logger: {
        debug: vi.fn(),
        error: vi.fn(),
        info: vi.fn(),
        warn: vi.fn(),
    },
}));

describe('auth service', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        discovery.mockResolvedValue({ issuer: 'mock-issuer' });
        randomPKCECodeVerifier.mockReturnValue('code-verifier');
        calculatePKCECodeChallenge.mockResolvedValue('code-challenge');
        buildAuthorizationUrl.mockReturnValue(new URL('https://keycloak.example/auth'));
        createAuthTransaction.mockResolvedValue(ok('txn-id'));
        deleteSession.mockResolvedValue(ok(undefined));
        updateSession.mockResolvedValue(ok(undefined));
        deleteAuthTransaction.mockResolvedValue(ok(undefined));
        tokenRevocation.mockResolvedValue(undefined);
    });

    it('starts login by creating a transaction and authorization url', async () => {
        const { login } = await import('./auth');
        const result = await login('/dashboard');

        expect(createAuthTransaction).toHaveBeenCalledWith({
            codeVerifier: 'code-verifier',
            redirectTo: '/dashboard',
            state: expect.any(String),
        });
        expect(buildAuthorizationUrl).toHaveBeenCalled();
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value.url).toBe('https://keycloak.example/auth');
            expect(result.value.sidTxnCookie).toBe('sid_txn=txn-id');
        }
    });

    it('rejects unsafe redirect targets when starting login', async () => {
        const { login } = await import('./auth');
        await login('https://evil.example');

        expect(createAuthTransaction).toHaveBeenCalledWith(
            expect.objectContaining({
                redirectTo: '/',
            }),
        );
    });

    it('returns Err when creating the auth transaction fails', async () => {
        createAuthTransaction.mockResolvedValue(err(new Error('redis unavailable')));

        const { login } = await import('./auth');
        const result = await login();

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toMatchObject({ message: 'redis unavailable' });
        }
    });

    it('exchanges an auth code for an auth payload', async () => {
        authorizationCodeGrant.mockResolvedValue({
            access_token: 'access',
            claims: () => ({ sub: 'user-sub' }),
            expires_in: 3600,
            id_token: 'id-token',
            refresh_token: 'refresh',
        });
        fetchUserInfo.mockResolvedValue({
            email: 'user@example.com',
            name: 'Test User',
            preferred_username: 'testuser',
            realm_roles: ['student'],
            sub: 'user-sub',
        });

        const { exchangeCodeForAuthPayload } = await import('./auth');
        const result = await exchangeCodeForAuthPayload(new URL('http://localhost/auth/callback?code=1&state=s'), {
            codeVerifier: 'verifier',
            redirectTo: '/dashboard',
            state: 's',
        });

        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value.accessToken).toBe('access');
            expect(result.value.refreshToken).toBe('refresh');
            expect(result.value.user.sub).toBe('user-sub');
            expect(result.value.user.roles).toEqual(['student']);
        }
    });

    it('returns Err when the token exchange fails', async () => {
        authorizationCodeGrant.mockRejectedValue(new Error('invalid_grant'));

        const { exchangeCodeForAuthPayload } = await import('./auth');
        const result = await exchangeCodeForAuthPayload(new URL('http://localhost/auth/callback?code=1&state=s'), {
            codeVerifier: 'verifier',
            redirectTo: '/',
            state: 's',
        });

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toMatchObject({ message: 'invalid_grant' });
        }
    });

    it('returns Err when no access token is received', async () => {
        authorizationCodeGrant.mockResolvedValue({
            claims: () => ({ sub: 'user-sub' }),
        });

        const { exchangeCodeForAuthPayload } = await import('./auth');
        const result = await exchangeCodeForAuthPayload(new URL('http://localhost/auth/callback?code=1&state=s'), {
            codeVerifier: 'verifier',
            redirectTo: '/',
            state: 's',
        });

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toMatchObject({ message: 'No access token received' });
        }
    });

    it('loads and consumes an auth transaction from the request', async () => {
        getSidTxnFromRequest.mockReturnValue('txn-id');
        getAuthTransaction.mockResolvedValue(
            ok({
                codeVerifier: 'verifier',
                redirectTo: '/me',
                state: 'state',
            }),
        );

        const { loadAuthTransactionFromRequest } = await import('./auth');
        const result = await loadAuthTransactionFromRequest(new Request('http://localhost/auth/callback'));

        expect(deleteAuthTransaction).toHaveBeenCalledWith('txn-id');
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toEqual({
                codeVerifier: 'verifier',
                redirectTo: '/me',
                state: 'state',
            });
        }
    });

    it('returns null when the sid_txn cookie is missing', async () => {
        getSidTxnFromRequest.mockReturnValue(null);

        const { loadAuthTransactionFromRequest } = await import('./auth');
        const result = await loadAuthTransactionFromRequest(new Request('http://localhost/auth/callback'));

        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toBeNull();
        }
    });

    it('builds a logout response and clears cookies', async () => {
        const { logout } = await import('./auth');
        const result = await logout(
            sampleSession({
                idToken: 'id-token',
                refreshToken: 'refresh',
            }),
        );

        expect(tokenRevocation).toHaveBeenCalledWith(expect.anything(), 'refresh', {
            token_type_hint: 'refresh_token',
        });
        expect(result.sidCookie).toBe('sid=; Max-Age=0');
        expect(result.sidTxnCookie).toBe('sid_txn=; Max-Age=0');
        expect(result.url).toContain('/protocol/openid-connect/logout');
        expect(result.url).toContain('id_token_hint=id-token');
    });

    it('requireAuthInLoader redirects when no session exists', async () => {
        getSessionEntryFromRequest.mockResolvedValue(ok(null));

        const { requireAuthInLoader } = await import('./auth');
        const result = await requireAuthInLoader(new Request('http://localhost/dashboard'));

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toBeInstanceOf(Response);
            expect(result.error.status).toBe(302);
            expect(result.error.headers.get('Location')).toContain('/login');
            expect(result.error.headers.get('Location')).toContain('redirectTo=%2Fdashboard');
        }
    });

    it('requireAuthInLoader redirects when the session store errors', async () => {
        getSessionEntryFromRequest.mockResolvedValue(err(new Error('redis down')));

        const { requireAuthInLoader } = await import('./auth');
        const result = await requireAuthInLoader(new Request('http://localhost/dashboard'));

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toBeInstanceOf(Response);
            expect(result.error.status).toBe(302);
        }
    });

    it('requireAuthInLoader returns a valid session', async () => {
        const session = sampleSession();
        getSessionEntryFromRequest.mockResolvedValue(ok({ id: 'sid-1', session }));

        const { requireAuthInLoader } = await import('./auth');
        const result = await requireAuthInLoader(new Request('http://localhost/dashboard'));

        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toEqual(session);
        }
    });

    it('requireAuthInLoader deletes and redirects expired sessions without refresh tokens', async () => {
        getSessionEntryFromRequest.mockResolvedValue(
            ok({
                id: 'sid-1',
                session: sampleSession({ expiresAt: Date.now() - 1000 }),
            }),
        );

        const { requireAuthInLoader } = await import('./auth');
        const result = await requireAuthInLoader(new Request('http://localhost/dashboard'));

        expect(deleteSession).toHaveBeenCalledWith('sid-1');
        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.status).toBe(302);
        }
    });

    it('getAccessToken returns null for a missing sid', async () => {
        getSessionEntryFromRequest.mockResolvedValue(ok(null));

        const { getAccessToken } = await import('./auth');
        await expect(getAccessToken(new Request('http://localhost/'))).resolves.toBeNull();
    });

    it('getAccessToken returns the current access token when still valid', async () => {
        getSessionEntryFromRequest.mockResolvedValue(
            ok({
                id: 'sid-1',
                session: sampleSession({ accessToken: 'current-token' }),
            }),
        );

        const { getAccessToken } = await import('./auth');
        await expect(getAccessToken(new Request('http://localhost/'))).resolves.toBe('current-token');
    });

    it('buildCallbackRequestUrl upgrades external http urls', async () => {
        const { buildCallbackRequestUrl } = await import('./auth');
        const url = buildCallbackRequestUrl(new Request('http://tutormatch.example/auth/callback'));

        expect(url.protocol).toBe('https:');
    });
});
