import type { ActionFunctionArgs, LoaderFunctionArgs } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { err, ok } from '~/.server/lib/result';
import { sampleSession } from '~/.server/test/fixtures';

const requireAuthInLoader = vi.fn();

vi.mock('~/.server/service/auth', () => ({
    requireAuthInLoader,
}));

function loaderArgs(url: string): LoaderFunctionArgs {
    const request = new Request(url);
    return {
        context: {},
        params: {},
        pattern: '/',
        request,
        url: new URL(request.url),
    };
}

function actionArgs(url: string): ActionFunctionArgs {
    const request = new Request(url, { method: 'POST' });
    return {
        context: {},
        params: {},
        pattern: '/',
        request,
        url: new URL(request.url),
    };
}

describe('routeProtection', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('redirects when unauthenticated', async () => {
        const redirect = new Response(null, { headers: { Location: '/login' }, status: 302 });
        requireAuthInLoader.mockResolvedValue(err(redirect));

        const { protectedLoader } = await import('./routeProtection');
        const loader = protectedLoader(async () => ({ ok: true }));

        await expect(loader(loaderArgs('http://localhost/dashboard'))).rejects.toBe(redirect);
    });

    it('runs the loader with the authenticated session', async () => {
        const session = sampleSession({ accessToken: 'tok' });
        requireAuthInLoader.mockResolvedValue(ok(session));

        const { protectedLoader } = await import('./routeProtection');
        const loader = protectedLoader(async ({ session: authed }) => ({
            sub: authed.user.sub,
            token: authed.accessToken,
        }));

        const result = await loader(loaderArgs('http://localhost/dashboard'));

        expect(result).toEqual({ sub: 'user-sub', token: 'tok' });
    });

    it('runs protected actions with the session', async () => {
        const session = sampleSession();
        requireAuthInLoader.mockResolvedValue(ok(session));

        const { protectedAction } = await import('./routeProtection');
        const action = protectedAction(async ({ session: authed }) => authed.user.roles);

        await expect(action(actionArgs('http://localhost/me'))).resolves.toEqual(['student']);
    });

    it('forbids role-protected loaders without the required role', async () => {
        requireAuthInLoader.mockResolvedValue(ok(sampleSession({ roles: ['student'] })));

        const { roleProtectedLoader } = await import('./routeProtection');
        const loader = roleProtectedLoader('admin', async () => ({ ok: true }));

        await expect(loader(loaderArgs('http://localhost/admin'))).rejects.toMatchObject({ status: 403 });
    });

    it('allows role-protected loaders with the required role', async () => {
        requireAuthInLoader.mockResolvedValue(ok(sampleSession({ roles: ['admin'] })));

        const { roleProtectedLoader } = await import('./routeProtection');
        const loader = roleProtectedLoader('admin', async () => ({ ok: true }));

        await expect(loader(loaderArgs('http://localhost/admin'))).resolves.toEqual({ ok: true });
    });
});
