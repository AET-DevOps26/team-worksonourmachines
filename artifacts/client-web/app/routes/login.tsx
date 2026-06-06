import type { LoaderFunctionArgs } from 'react-router';
import { Link, redirect, useLoaderData } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { safeRedirectPath } from '~/.server/lib/redirect';
import { getSession } from '~/.server/service/session';

const AUTH_ERRORS: Record<string, string> = {
    auth_failed: 'Sign-in failed. Please try again.',
    invalid_auth_state: 'The sign-in session expired or was invalid. Please try again.',
    no_code: 'No authorization code was returned. Please try again.',
};

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);
    const sessionResult = await getSession(request);
    if (sessionResult.isErr) {
        logger.error('Failed to load session on login page', { error: sessionResult.error });
    }

    const session = sessionResult.isOk ? sessionResult.value : null;
    if (session) {
        return redirect(safeRedirectPath(requestUrl.searchParams.get('redirectTo')));
    }

    const redirectTo = safeRedirectPath(requestUrl.searchParams.get('redirectTo'));
    const loginHref = redirectTo === '/' ? '/auth/login' : `/auth/login?redirectTo=${encodeURIComponent(redirectTo)}`;

    return {
        authError: AUTH_ERRORS[requestUrl.searchParams.get('error') ?? ''] ?? null,
        loginHref,
    };
}

export default function LoginRoute() {
    const { authError, loginHref } = useLoaderData<typeof loader>();

    return (
        <main className="min-h-svh bg-zinc-950 px-6 py-10 text-zinc-100">
            <div className="mx-auto flex w-full max-w-lg flex-col gap-8">
                <header className="flex flex-col gap-3">
                    <p className="text-sm font-medium uppercase tracking-wide text-emerald-300">TUtorMatch</p>
                    <h1 className="text-3xl font-semibold tracking-normal text-white">Sign in</h1>
                    <p className="text-base leading-7 text-zinc-300">
                        Continue with your Keycloak account to access TUtorMatch.
                    </p>
                </header>

                {authError ? (
                    <div className="rounded-md border border-red-400/40 bg-red-950/40 px-4 py-3 text-sm text-red-100">
                        {authError}
                    </div>
                ) : null}

                <section className="flex flex-col gap-5 rounded-md border border-zinc-800 bg-zinc-900 p-6 shadow-xl shadow-black/20">
                    <a
                        className="inline-flex w-fit items-center rounded-md bg-emerald-400 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-emerald-300"
                        href={loginHref}
                    >
                        Sign in with Keycloak
                    </a>

                    <Link className="text-sm text-zinc-400 transition hover:text-zinc-200" to="/">
                        Back to home
                    </Link>

                    <div className="rounded-md bg-zinc-950 p-4 text-sm text-zinc-300">
                        <p className="font-medium text-zinc-100">Demo credentials</p>
                        <p className="mt-2 font-mono">lukas.student@example.com / Tutormatch123!</p>
                        <p className="mt-1 font-mono">anna.tutor@example.com / Tutormatch123!</p>
                        <p className="mt-1 font-mono">admin.tutormatch@example.com / Tutormatch123!</p>
                    </div>
                </section>
            </div>
        </main>
    );
}
