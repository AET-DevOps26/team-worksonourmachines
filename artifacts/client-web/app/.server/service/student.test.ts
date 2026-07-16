import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ErrorResponse } from '~/.server/api/error';

const getMyProfile = vi.fn();
const createGoal = vi.fn();
const listMyGoals = vi.fn();

vi.mock('~/.server/api', () => ({
    studentApi: {
        createGoal,
        getMyProfile,
        listMyGoals,
    },
}));

describe('student service', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('returns Ok for a successful profile fetch', async () => {
        const profile = { displayName: 'Ada', userId: 'u1' };
        getMyProfile.mockResolvedValue(profile);

        const { getStudentProfile } = await import('./student');
        const result = await getStudentProfile();

        expect(getMyProfile).toHaveBeenCalledOnce();
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toEqual(profile);
        }
    });

    it('maps API ErrorResponse to Err', async () => {
        createGoal.mockRejectedValue(new ErrorResponse('badRequest'));

        const { createGoal: createGoalService } = await import('./student');
        const result = await createGoalService({
            description: 'Learn graphs',
            moduleCode: 'IN0001',
            title: 'Graphs',
        } as never);

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('badRequest');
        }
    });

    it('maps unexpected throws to Err(unknown)', async () => {
        listMyGoals.mockRejectedValue('weird');

        const { listMyGoals: listMyGoalsService } = await import('./student');
        const result = await listMyGoalsService();

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('unknown');
        }
    });
});
