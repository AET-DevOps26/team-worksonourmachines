import { describe, expect, it } from 'vitest';
import { type ApiErrorType, routeErrorJson, toRouteError } from './routeError';

describe('route errors', () => {
    it.each([
        ['badRequest', 400],
        ['unauthorized', 401],
        ['forbidden', 403],
        ['notFound', 404],
        ['internalServerError', 500],
        ['unknown', 500],
        ['serviceUnavailable', 503],
    ] satisfies [ApiErrorType, number][])('maps %s to HTTP %i', (type, status) => {
        expect(toRouteError({ type })).toMatchObject({ status });
    });

    it('does not expose upstream error details', () => {
        const upstreamError = {
            detail: 'database host: secret.internal',
            type: 'internalServerError' as const,
        };
        const mapped = toRouteError(upstreamError);

        expect(mapped.payload).toEqual({
            code: 'internalServerError',
            message: 'Something went wrong. Please try again.',
        });
        expect(JSON.stringify(mapped)).not.toContain('secret.internal');
    });

    it('creates a JSON response for resource routes', async () => {
        const response = routeErrorJson({ type: 'serviceUnavailable' });

        expect(response.status).toBe(503);
        expect(response.headers.get('content-type')).toContain('application/json');
        await expect(response.json()).resolves.toEqual({
            code: 'serviceUnavailable',
            message: 'This service is temporarily unavailable. Please try again.',
        });
    });
});
