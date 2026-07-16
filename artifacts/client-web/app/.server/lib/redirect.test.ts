import { describe, expect, it } from 'vitest';
import { safeRedirectPath, toExternalUrl } from './redirect';

describe('redirect', () => {
    describe('safeRedirectPath', () => {
        it('allows relative app paths', () => {
            expect(safeRedirectPath('/dashboard')).toBe('/dashboard');
            expect(safeRedirectPath('/me/profile?tab=goals')).toBe('/me/profile?tab=goals');
        });

        it('rejects null, empty, and off-site values', () => {
            expect(safeRedirectPath(null)).toBe('/');
            expect(safeRedirectPath('')).toBe('/');
            expect(safeRedirectPath('https://evil.example')).toBe('/');
            expect(safeRedirectPath('//evil.example')).toBe('/');
            expect(safeRedirectPath('dashboard')).toBe('/');
        });
    });

    describe('toExternalUrl', () => {
        it('upgrades non-localhost http requests to https', () => {
            const url = toExternalUrl(new Request('http://tutormatch.example/auth/callback?code=1'));

            expect(url.protocol).toBe('https:');
            expect(url.hostname).toBe('tutormatch.example');
            expect(url.searchParams.get('code')).toBe('1');
        });

        it('keeps localhost on http', () => {
            const url = toExternalUrl(new Request('http://localhost:5173/auth/callback'));

            expect(url.protocol).toBe('http:');
            expect(url.hostname).toBe('localhost');
        });
    });
});
