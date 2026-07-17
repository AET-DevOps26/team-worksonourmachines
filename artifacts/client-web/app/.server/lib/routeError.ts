import { data } from 'react-router';
import { logger } from '~/.server/lib/logger';

export type ApiErrorType =
    | 'badRequest'
    | 'forbidden'
    | 'internalServerError'
    | 'notFound'
    | 'serviceUnavailable'
    | 'unauthorized'
    | 'unknown';

type RouteErrorPayload = {
    code: ApiErrorType;
    message: string;
};

type ApiError = {
    readonly type: ApiErrorType;
};

const errorConfiguration: Record<ApiErrorType, { message: string; status: number }> = {
    badRequest: { message: 'The request could not be completed.', status: 400 },
    forbidden: { message: 'You do not have permission to view this page.', status: 403 },
    internalServerError: { message: 'Something went wrong. Please try again.', status: 500 },
    notFound: { message: 'We could not find what you were looking for.', status: 404 },
    serviceUnavailable: { message: 'This service is temporarily unavailable. Please try again.', status: 503 },
    unauthorized: { message: 'Your session has expired. Please sign in again.', status: 401 },
    unknown: { message: 'Something went wrong. Please try again.', status: 500 },
};

export function toRouteError(error: ApiError): { payload: RouteErrorPayload; status: number } {
    const configuration = errorConfiguration[error.type];
    return {
        payload: { code: error.type, message: configuration.message },
        status: configuration.status,
    };
}

export function routeErrorJson(error: ApiError): Response {
    const { payload, status } = toRouteError(error);
    logServerError(error, status);
    return Response.json(payload, { status });
}

export function throwRouteError(error: ApiError): never {
    const { payload, status } = toRouteError(error);
    logServerError(error, status);
    throw data(payload, { status });
}

function logServerError(error: ApiError, status: number): void {
    if (status >= 500) {
        logger.error('Upstream API request failed', { code: error.type, status });
    }
}
