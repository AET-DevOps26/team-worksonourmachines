import { env } from '~/.server/lib/env';
import { resolveAccessTokenForActiveRequest } from '~/.server/lib/requestAuth';
import { errorMiddlewareConfiguration } from '../error';
import { Configuration, DefaultApi } from './generated';

export const communicationApi = new DefaultApi(
    new Configuration({
        accessToken: () => resolveAccessTokenForActiveRequest().then((token) => token ?? ''),
        basePath: env.get('SERVER_COMMUNICATION_API_URL'),
        middleware: [errorMiddlewareConfiguration()],
    }),
);
