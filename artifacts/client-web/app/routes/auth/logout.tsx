import type { LoaderFunctionArgs } from 'react-router';
import { redirect } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { logout } from '~/.server/service/auth';
import { destroySession, getSession } from '~/.server/service/session';

export async function loader({ request }: LoaderFunctionArgs) {
    const sessionResult = await getSession(request);
    if (sessionResult.isErr) {
        logger.error('Failed to load session for logout', { error: sessionResult.error });
    }

    const { sidCookie, sidTxnCookie, url } = await logout(sessionResult.isOk ? sessionResult.value : null);
    const destroyResult = await destroySession(request);
    if (destroyResult.isErr) {
        logger.error('Failed to destroy session during logout', { error: destroyResult.error });
    }

    const headers = new Headers();
    headers.append('Set-Cookie', sidCookie);
    headers.append('Set-Cookie', sidTxnCookie);

    return redirect(url, { headers });
}
