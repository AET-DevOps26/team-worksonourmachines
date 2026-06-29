import { ErrorResponse, type ErrorType } from '~/.server/api/error';
import { type AsyncResult, err, ok } from '~/.server/lib/result';

export async function callApi<T>(fn: () => Promise<T>): AsyncResult<T, ErrorResponse<ErrorType>> {
    try {
        return ok(await fn());
    } catch (error) {
        if (error instanceof ErrorResponse) {
            return err(error);
        }
        return err(new ErrorResponse('unknown'));
    }
}

export function isNotFound(error: ErrorResponse<ErrorType>): boolean {
    return error.type === 'notFound';
}
