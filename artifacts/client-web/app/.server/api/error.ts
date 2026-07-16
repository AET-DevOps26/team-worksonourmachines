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
    readonly detail: string | undefined;

    constructor(type: T, detail?: string) {
        super(`ErrorResponse(${type})`);
        this.type = type;
        this.detail = detail;
    }
}

class FetchError extends ErrorResponse<'unknown'> {
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

            if (context.response.status === HttpStatusCode.BadRequest) {
                let detail: string | undefined;
                try {
                    const body: unknown = await context.response.json();
                    if (typeof body === 'object' && body !== null) {
                        const b = body as Record<string, unknown>;
                        if ('detail' in b && typeof b.detail === 'string') {
                            detail = b.detail;
                        } else if ('message' in b && typeof b.message === 'string') {
                            detail = b.message;
                        }
                    }
                } catch {}
                throw new ErrorResponse('badRequest', detail);
            }

            if (context.response.status === HttpStatusCode.ServiceUnavailable) {
                throw new ErrorResponse('serviceUnavailable');
            }

            if (context.response.status === HttpStatusCode.UnprocessableEntity) {
                throw new ErrorResponse('unknown');
            }

            if (context.response.status === HttpStatusCode.InternalServerError) {
                throw new ErrorResponse('internalServerError');
            }

            throw new ErrorResponse('unknown');
        },
    };
}
