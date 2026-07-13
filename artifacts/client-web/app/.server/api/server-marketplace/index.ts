import { env } from '~/.server/lib/env';
import { resolveAccessTokenForActiveRequest } from '~/.server/lib/requestAuth';
import { errorMiddlewareConfiguration } from '../error';
import { Configuration, DefaultApi } from './generated';

export const marketplaceApi = new DefaultApi(
    new Configuration({
        accessToken: () => resolveAccessTokenForActiveRequest().then((token) => `Bearer ${token}`),
        basePath: env.get('SERVER_MARKETPLACE_API_URL'),
        middleware: [errorMiddlewareConfiguration()],
    }),
);
