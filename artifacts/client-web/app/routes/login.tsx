import type { LoaderFunctionArgs } from 'react-router';
import { Link, redirect, useLoaderData } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { safeRedirectPath } from '~/.server/lib/redirect';
import { getSession } from '~/.server/service/session';
import { Logo } from '~/components/shell';
import { buttonVariants } from '~/components/ui/button';
import { cn } from '~/lib/ui/utils';

const AUTH_ERRORS: Record<string, string> = {
    auth_failed: 'Sign-in failed. Please try again.',
    invalid_auth_state: 'The sign-in session expired or was invalid. Please try again.',
    no_code: 'No authorization code was returned. Please try again.',
};

const DEFAULT_REGISTER_REDIRECT = encodeURIComponent('/me/profile?edit=1');

function buildRegisterHref(redirectTo: string): string {
    return redirectTo === '/'
        ? `/auth/register?redirectTo=${DEFAULT_REGISTER_REDIRECT}`
        : `/auth/register?redirectTo=${encodeURIComponent(redirectTo)}`;
}

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
    const registerHref = buildRegisterHref(redirectTo);

    return {
        authError: AUTH_ERRORS[requestUrl.searchParams.get('error') ?? ''] ?? null,
        loginHref,
        registerHref,
    };
}

export default function LoginRoute() {
    const { authError, loginHref, registerHref } = useLoaderData<typeof loader>();

    return (
        <div className="flex min-h-svh flex-col bg-background text-foreground">
            <header className="sticky top-0 z-40 border-b border-border/60 bg-background/80 backdrop-blur-md">
                <div className="mx-auto grid h-14 max-w-6xl grid-cols-[auto_minmax(0,1fr)_auto] items-center px-6">
                    <Logo />
                </div>
            </header>

            <main className="flex flex-1 items-center justify-center px-6 py-12">
                <div className="flex w-full max-w-md flex-col gap-8">
                    <header className="flex flex-col gap-2">
                        <h1 className="text-2xl font-semibold tracking-tight">Sign in</h1>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Continue with your Keycloak account to access TUtorMatch.
                        </p>
                    </header>

                    {authError ? (
                        <div className="rounded-md border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                            {authError}
                        </div>
                    ) : null}

                    <section className="flex flex-col gap-6 rounded-xl border border-border/60 bg-card/70 p-8 shadow-sm backdrop-blur-sm">
                        <a className={cn(buttonVariants({ size: 'lg' }), 'w-full justify-center')} href={loginHref}>
                            Sign in with Keycloak
                        </a>

                        <a
                            className={cn(buttonVariants({ size: 'lg', variant: 'outline' }), 'w-full justify-center')}
                            href={registerHref}
                        >
                            Create an account
                        </a>

                        <Link className="text-center text-sm text-muted-foreground hover:text-primary" to="/">
                            Back to home
                        </Link>

                        <div className="rounded-lg border border-border/50 bg-secondary/40 p-4 text-sm text-muted-foreground">
                            <p className="font-medium text-foreground">Demo credentials</p>
                            <p className="mt-2 font-mono text-xs">lukas.student@example.com / Tutormatch123!</p>
                            <p className="mt-1 font-mono text-xs">anna.tutor@example.com / Tutormatch123!</p>
                            <p className="mt-1 font-mono text-xs">admin.tutormatch@example.com / Tutormatch123!</p>
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}
