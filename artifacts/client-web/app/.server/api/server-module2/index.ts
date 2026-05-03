import { errorMiddlewareConfiguration } from '~/.server/api/error';
import { env } from '~/.server/lib/env';
import { Configuration, DefaultApi } from './generated';

export const module2Api = new DefaultApi(
    new Configuration({
        basePath: env.get('SERVER_MODULE2_API_URL'),
        middleware: [errorMiddlewareConfiguration()],
    }),
);
