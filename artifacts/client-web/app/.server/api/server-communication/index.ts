import { errorMiddlewareConfiguration } from '~/.server/api/error';
import { env } from '~/.server/lib/env';
import { Configuration, DefaultApi } from './generated';

export const communicationApi = new DefaultApi(
    new Configuration({
        basePath: env.get('SERVER_COMMUNICATION_API_URL'),
        middleware: [errorMiddlewareConfiguration()],
    }),
);
