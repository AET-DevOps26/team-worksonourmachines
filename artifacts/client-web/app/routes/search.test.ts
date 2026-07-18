import { beforeEach, describe, expect, it, vi } from 'vitest';
import { err } from '~/.server/lib/result';
import { listModules, listTutors } from '~/.server/service/marketplace';

vi.mock('~/.server/service/marketplace', () => ({
    listModules: vi.fn(),
    listTutors: vi.fn(),
}));

vi.mock('~/.server/service/routeProtection', () => ({
    protectedLoader: (loader: unknown) => loader,
}));

describe('search resource route', () => {
    beforeEach(() => {
        vi.resetAllMocks();
    });

    it('returns a typed JSON error instead of throwing an HTML route error', async () => {
        vi.mocked(listModules).mockResolvedValue(err({ type: 'serviceUnavailable' }) as never);
        vi.mocked(listTutors).mockResolvedValue(err({ type: 'serviceUnavailable' }) as never);
        const { loader } = await import('./search');

        const response = await loader({
            request: new Request('http://localhost/search?q=math'),
        } as never);

        expect(response).toBeInstanceOf(Response);
        expect((response as Response).status).toBe(503);
        await expect((response as Response).json()).resolves.toMatchObject({ code: 'serviceUnavailable' });
    });
});
