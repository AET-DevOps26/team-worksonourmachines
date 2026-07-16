import { describe, expect, it } from 'vitest';
import { ErrorResponse } from '~/.server/api/error';
import { callApi, isNotFound } from './apiCall';

describe('callApi', () => {
    it('returns Ok on success', async () => {
        const result = await callApi(async () => ({ id: '1' }));

        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toEqual({ id: '1' });
        }
    });

    it('maps ErrorResponse throws to Err without rethrowing', async () => {
        const result = await callApi(async () => {
            throw new ErrorResponse('notFound');
        });

        expect(result.isOk).toBe(false);
        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toBeInstanceOf(ErrorResponse);
            expect(result.error.type).toBe('notFound');
            expect(isNotFound(result.error)).toBe(true);
        }
    });

    it('maps unknown throws to ErrorResponse(unknown)', async () => {
        const result = await callApi(async () => {
            throw new Error('boom');
        });

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('unknown');
        }
    });
});
