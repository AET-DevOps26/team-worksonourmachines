import type { LoaderFunctionArgs } from 'react-router';
import { redirect } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { safeRedirectPath } from '~/.server/lib/redirect';
import { register } from '~/.server/service/auth';

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);
    const redirectTo = safeRedirectPath(requestUrl.searchParams.get('redirectTo'));
    const registerResult = await register(redirectTo);

    if (registerResult.isErr) {
        logger.error('Failed to start registration flow', { error: registerResult.error });
        const params = new URLSearchParams({ error: 'auth_failed', redirectTo });
        return redirect(`/login?${params.toString()}`);
    }

    const { sidTxnCookie, url } = registerResult.value;

    return redirect(url, {
        headers: { 'Set-Cookie': sidTxnCookie },
    });
}
