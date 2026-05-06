import type { Middleware } from '~/.server/api/server-communication/generated';
import { HttpStatusCode } from '~/.server/lib/http';

export type ErrorType =
    | 'badRequest'
    | 'forbidden'
    | 'internalServerError'
    | 'notFound'
    | 'serviceUnavailable'
    | 'unauthorized'
    | 'unknown';

export class ErrorResponse<T extends ErrorType> extends Error {
    readonly type: T;

    constructor(type: T) {
        super(`ErrorResponse(${type})`);
        this.type = type;
    }
}

export class FetchError extends ErrorResponse<'unknown'> {
    constructor() {
        super('unknown');
    }
}

export function errorMiddlewareConfiguration(): Middleware {
    return {
        onError: async () => {
            throw new FetchError();
        },
        post: async (context) => {
            if (context.response.ok) {
                return context.response;
            }

            if (context.response.status === HttpStatusCode.Unauthorized) {
                throw new ErrorResponse('unauthorized');
            }

            if (context.response.status === HttpStatusCode.Forbidden) {
                throw new ErrorResponse('forbidden');
            }

            if (context.response.status === HttpStatusCode.NotFound) {
                throw new ErrorResponse('notFound');
            }

            if (context.response.status === HttpStatusCode.ServiceUnavailable) {
                throw new ErrorResponse('serviceUnavailable');
            }

            if (context.response.status === HttpStatusCode.InternalServerError) {
                throw new ErrorResponse('internalServerError');
            }

            throw new ErrorResponse('unknown');
        },
    };
}
