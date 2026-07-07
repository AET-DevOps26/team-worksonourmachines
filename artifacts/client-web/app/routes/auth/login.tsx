import type { LoaderFunctionArgs } from 'react-router';
import { redirect } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { safeRedirectPath } from '~/.server/lib/redirect';
import { login } from '~/.server/service/auth';

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);
    const redirectTo = requestUrl.searchParams.get('redirectTo');
    const loginResult = await login(safeRedirectPath(redirectTo ?? '/dashboard'));

    if (loginResult.isErr) {
        logger.error('Failed to start login flow', { error: loginResult.error });
        return redirect('/login?error=auth_failed');
    }

    const { sidTxnCookie, url } = loginResult.value;

    return redirect(url, {
        headers: { 'Set-Cookie': sidTxnCookie },
    });
}
