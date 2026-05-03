import { errorMiddlewareConfiguration } from '~/.server/api/error';
import { env } from '~/.server/lib/env';
import { Configuration, DefaultApi } from './generated';

export const coursesApi = new DefaultApi(
    new Configuration({
        basePath: env.get('SERVER_COURSES_API_URL'),
        middleware: [errorMiddlewareConfiguration()],
    }),
);
