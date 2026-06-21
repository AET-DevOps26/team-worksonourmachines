import type { LoaderFunctionArgs } from 'react-router';
import { redirect } from 'react-router';
import { logger } from '~/.server/lib/logger';
import {
    buildCallbackRequestUrl,
    exchangeCodeForAuthPayload,
    loadAuthTransactionFromRequest,
} from '~/.server/service/auth';
import { clearSidTxnCookie, createSessionFromAuthPayload, createSidCookie } from '~/.server/service/session';

function redirectToLogin(error: 'auth_failed' | 'invalid_auth_state' | 'no_code') {
    return redirect(`/login?error=${error}`, {
        headers: { 'Set-Cookie': clearSidTxnCookie() },
    });
}

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);
    const code = requestUrl.searchParams.get('code');
    const oidcError = requestUrl.searchParams.get('error');

    if (oidcError) {
        logger.error('Keycloak rejected the login request', { error: oidcError });
        return redirectToLogin('auth_failed');
    }

    if (!code) {
        return redirectToLogin('no_code');
    }

    const transactionResult = await loadAuthTransactionFromRequest(request);
    if (transactionResult.isErr) {
        logger.error('Failed to load auth transaction', { error: transactionResult.error });
        return redirectToLogin('invalid_auth_state');
    }

    const transaction = transactionResult.value;
    const returnedState = requestUrl.searchParams.get('state');

    if (!transaction || !returnedState || returnedState !== transaction.state) {
        logger.error('Invalid auth transaction', { returnedState });
        return redirectToLogin('invalid_auth_state');
    }

    const exchangeResult = await exchangeCodeForAuthPayload(buildCallbackRequestUrl(request), transaction);
    if (exchangeResult.isErr) {
        logger.error('Failed to exchange authorization code', { error: exchangeResult.error });
        return redirectToLogin('auth_failed');
    }

    const persistResult = await createSessionFromAuthPayload(exchangeResult.value);
    if (persistResult.isErr) {
        return redirectToLogin('auth_failed');
    }

    const headers = new Headers();
    headers.append('Set-Cookie', createSidCookie(persistResult.value));
    headers.append('Set-Cookie', clearSidTxnCookie());

    return redirect(transaction.redirectTo, { headers });
}
