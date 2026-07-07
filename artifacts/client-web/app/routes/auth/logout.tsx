import type { LoaderFunctionArgs } from 'react-router';
import { redirect } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { logout } from '~/.server/service/auth';
import { protectedAction } from '~/.server/service/routeProtection';
import { destroySession } from '~/.server/service/session';

export async function loader(_args: LoaderFunctionArgs) {
    return redirect('/');
}

export const action = protectedAction(async ({ request, session }) => {
    const { sidCookie, sidTxnCookie, url } = await logout(session);
    const destroyResult = await destroySession(request);
    if (destroyResult.isErr) {
        logger.error('Failed to destroy session during logout', { error: destroyResult.error });
    }

    const headers = new Headers();
    headers.append('Set-Cookie', sidCookie);
    headers.append('Set-Cookie', sidTxnCookie);

    return redirect(url, { headers });
});
