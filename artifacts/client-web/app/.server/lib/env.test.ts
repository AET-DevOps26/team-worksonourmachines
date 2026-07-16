import { afterEach, describe, expect, it, vi } from 'vitest';
import { applyValidEnv, validEnv } from '~/.server/test/fixtures';

describe('env', () => {
    afterEach(() => {
        vi.resetModules();
        applyValidEnv();
    });

    it('exposes validated values and derived flags', async () => {
        applyValidEnv({ APP_BASE_URL: 'https://tutormatch.example', NODE_ENV: 'production' });
        const { env } = await import('./env');

        expect(env.get('KEYCLOAK_CLIENT_ID')).toBe(validEnv.KEYCLOAK_CLIENT_ID);
        expect(env.get('REDIS_URL')).toBe(validEnv.REDIS_URL);
        expect(env.isDev).toBe(false);
        expect(env.isSecureCookies).toBe(true);
    });

    it('rejects a missing required variable', async () => {
        applyValidEnv();
        delete process.env.APP_BASE_URL;

        await expect(import('./env')).rejects.toThrow();
    });

    it('rejects an invalid enum value', async () => {
        applyValidEnv({ LOG_LEVEL: 'trace' });

        await expect(import('./env')).rejects.toThrow();
    });

    it('treats http APP_BASE_URL as insecure cookies', async () => {
        applyValidEnv({ APP_BASE_URL: 'http://localhost:5173' });
        const { env } = await import('./env');

        expect(env.isSecureCookies).toBe(false);
        expect(env.isDev).toBe(true);
    });
});
