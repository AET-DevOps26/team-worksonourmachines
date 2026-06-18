import { AsyncLocalStorage } from 'node:async_hooks';

const requestAccessTokenStorage = new AsyncLocalStorage<string | undefined>();

export function runWithRequestAccessToken<T>(
    accessToken: string | undefined,
    fn: () => T | Promise<T>,
): T | Promise<T> {
    return requestAccessTokenStorage.run(accessToken, fn);
}

export function resolveAccessTokenForActiveRequest(): Promise<string | undefined> {
    return Promise.resolve(requestAccessTokenStorage.getStore());
}
