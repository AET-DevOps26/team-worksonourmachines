import { AsyncLocalStorage } from 'node:async_hooks';
import { getAccessToken } from '~/.server/service/auth';

const requestStorage = new AsyncLocalStorage<Request>();

export function runWithAuthenticatedRequest<T>(request: Request, fn: () => T | Promise<T>): T | Promise<T> {
    return requestStorage.run(request, fn);
}

export function getAuthenticatedRequest(): Request | undefined {
    return requestStorage.getStore();
}

export async function resolveAccessTokenForActiveRequest(): Promise<string | undefined> {
    const request = requestStorage.getStore();
    if (!request) {
        return undefined;
    }

    const token = await getAccessToken(request);
    return token ?? undefined;
}
