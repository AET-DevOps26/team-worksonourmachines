import { afterEach, describe, expect, it, vi } from 'vitest';
import { applyValidEnv } from '~/.server/test/fixtures';

describe('cookie', () => {
    afterEach(() => {
        vi.resetModules();
        applyValidEnv();
    });

    it('reads a present cookie value', async () => {
        const { getCookieValue } = await import('./cookie');
        const request = new Request('http://localhost/', {
            headers: { cookie: 'sid=abc123; other=value' },
        });

        expect(getCookieValue(request, 'sid')).toBe('abc123');
        expect(getCookieValue(request, 'missing')).toBeNull();
    });

    it('treats empty cookie values as missing', async () => {
        const { getCookieValue } = await import('./cookie');
        const request = new Request('http://localhost/', {
            headers: { cookie: 'sid=' },
        });

        expect(getCookieValue(request, 'sid')).toBeNull();
    });

    it('serializes httpOnly cookies with secure=false for http base urls', async () => {
        applyValidEnv({ APP_BASE_URL: 'http://localhost:5173' });
        const { serializeCookie } = await import('./cookie');

        const header = serializeCookie({ maxAge: 60, name: 'sid', value: 'session-id' });

        expect(header).toContain('sid=session-id');
        expect(header).toContain('HttpOnly');
        expect(header).toContain('Path=/');
        expect(header).toContain('SameSite=Lax');
        expect(header).not.toContain('Secure');
    });

    it('serializes secure cookies for https base urls', async () => {
        applyValidEnv({ APP_BASE_URL: 'https://tutormatch.example' });
        const { serializeCookie } = await import('./cookie');

        const header = serializeCookie({ maxAge: 60, name: 'sid', value: 'session-id' });

        expect(header).toContain('Secure');
    });
});
