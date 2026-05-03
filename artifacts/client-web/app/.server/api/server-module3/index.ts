import { errorMiddlewareConfiguration } from '~/.server/api/error';
import { env } from '~/.server/lib/env';
import { Configuration, DefaultApi } from './generated';

export const module3Api = new DefaultApi(
    new Configuration({
        basePath: env.get('SERVER_MODULE3_API_URL'),
        middleware: [errorMiddlewareConfiguration()],
    }),
);
