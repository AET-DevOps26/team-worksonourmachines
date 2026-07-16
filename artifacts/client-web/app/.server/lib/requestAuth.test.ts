import { describe, expect, it } from 'vitest';
import { resolveAccessTokenForActiveRequest, runWithRequestAccessToken } from './requestAuth';

describe('requestAuth', () => {
    it('stores and resolves the access token for the active request', async () => {
        const value = await runWithRequestAccessToken('token-123', async () => {
            return resolveAccessTokenForActiveRequest();
        });

        expect(value).toBe('token-123');
    });

    it('returns undefined outside a request context', async () => {
        await expect(resolveAccessTokenForActiveRequest()).resolves.toBeUndefined();
    });

    it('isolates nested request contexts', async () => {
        const outer = await runWithRequestAccessToken('outer', async () => {
            const nested = await runWithRequestAccessToken('inner', () => resolveAccessTokenForActiveRequest());
            const afterNested = await resolveAccessTokenForActiveRequest();
            return { afterNested, nested };
        });

        expect(outer).toEqual({ afterNested: 'outer', nested: 'inner' });
    });
});
