import type { LoaderFunctionArgs } from 'react-router';
import { redirect } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { safeRedirectPath } from '~/.server/lib/redirect';
import { register } from '~/.server/service/auth';

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);
    const redirectTo = requestUrl.searchParams.get('redirectTo');
    const registerResult = await register(safeRedirectPath(redirectTo));

    if (registerResult.isErr) {
        logger.error('Failed to start registration flow', { error: registerResult.error });
        return redirect('/login?error=auth_failed');
    }

    const { sidTxnCookie, url } = registerResult.value;

    return redirect(url, {
        headers: { 'Set-Cookie': sidTxnCookie },
    });
}
