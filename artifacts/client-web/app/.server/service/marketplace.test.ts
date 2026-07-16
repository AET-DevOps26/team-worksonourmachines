import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ErrorResponse } from '~/.server/api/error';

const listModules = vi.fn();
const getModule = vi.fn();
const listTutors = vi.fn();

vi.mock('~/.server/api', () => ({
    marketplaceApi: {
        getModule,
        listModules,
        listTutors,
    },
}));

describe('marketplace service', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('returns Ok for a successful listModules call', async () => {
        const page = { items: [{ code: 'IN0001' }], page: 1, pageSize: 20, total: 1 };
        listModules.mockResolvedValue(page);

        const { listModules: listModulesService } = await import('./marketplace');
        const result = await listModulesService({ page: 1, q: 'algo' });

        expect(listModules).toHaveBeenCalledWith({ page: 1, q: 'algo' });
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toEqual(page);
        }
    });

    it('maps API ErrorResponse to Err', async () => {
        getModule.mockRejectedValue(new ErrorResponse('notFound'));

        const { getModule: getModuleService } = await import('./marketplace');
        const result = await getModuleService('missing');

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('notFound');
        }
    });

    it('maps unexpected throws to Err(unknown)', async () => {
        listTutors.mockRejectedValue(new Error('socket hang up'));

        const { listTutors: listTutorsService } = await import('./marketplace');
        const result = await listTutorsService();

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('unknown');
        }
    });
});
