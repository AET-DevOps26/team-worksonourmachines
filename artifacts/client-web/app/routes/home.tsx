import type { LoaderFunctionArgs } from 'react-router';
import { useLoaderData } from 'react-router';
import { getAuthenticatedUser } from '~/.server/service/keycloak';

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);

    return {
        authError: requestUrl.searchParams.get('auth_error'),
        user: await getAuthenticatedUser(request),
    };
}

export default function HomeRoute() {
    const { authError, user } = useLoaderData<typeof loader>();

    // This is a very basic UI to demonstrate the authentication state and show how to trigger the login and logout flows.
    // Just meant as a reference for now and can be tweaked as per needs.
    return (
        <main className="min-h-svh bg-zinc-950 px-6 py-10 text-zinc-100">
            <div className="mx-auto flex w-full max-w-4xl flex-col gap-8">
                <header className="flex flex-col gap-3">
                    <p className="text-sm font-medium uppercase tracking-wide text-emerald-300">TUtorMatch</p>
                    <h1 className="text-3xl font-semibold tracking-normal text-white">Keycloak Login Demo</h1>
                    <p className="max-w-2xl text-base leading-7 text-zinc-300">
                        This page uses the local Keycloak realm to authenticate one of the imported demo users.
                    </p>
                </header>

                {authError ? (
                    <div className="rounded-md border border-red-400/40 bg-red-950/40 px-4 py-3 text-sm text-red-100">
                        {authError}
                    </div>
                ) : null}

                <section className="rounded-md border border-zinc-800 bg-zinc-900 p-6 shadow-xl shadow-black/20">
                    {user ? (
                        <div className="flex flex-col gap-5">
                            <div>
                                <p className="text-sm font-medium text-emerald-300">Signed in</p>
                                <h2 className="mt-1 text-2xl font-semibold text-white">{user.name ?? user.username}</h2>
                                <p className="mt-1 text-sm text-zinc-400">{user.email ?? user.username}</p>
                            </div>

                            <dl className="grid gap-4 sm:grid-cols-2">
                                <div className="rounded-md bg-zinc-950 p-4">
                                    <dt className="text-xs font-medium uppercase tracking-wide text-zinc-500">
                                        Subject
                                    </dt>
                                    <dd className="mt-2 break-all font-mono text-sm text-zinc-200">{user.sub}</dd>
                                </div>
                                <div className="rounded-md bg-zinc-950 p-4">
                                    <dt className="text-xs font-medium uppercase tracking-wide text-zinc-500">
                                        Realm roles
                                    </dt>
                                    <dd className="mt-2 flex flex-wrap gap-2">
                                        {user.roles.map((role) => (
                                            <span
                                                className="rounded bg-emerald-500 px-2 py-1 text-xs font-semibold text-zinc-950"
                                                key={role}
                                            >
                                                {role}
                                            </span>
                                        ))}
                                    </dd>
                                </div>
                            </dl>

                            <a
                                className="inline-flex w-fit items-center rounded-md bg-zinc-100 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-white"
                                href="/auth/keycloak/logout"
                            >
                                Sign out
                            </a>
                        </div>
                    ) : (
                        <div className="flex flex-col gap-5">
                            <div>
                                <p className="text-sm font-medium text-zinc-400">Anonymous session</p>
                                <h2 className="mt-1 text-2xl font-semibold text-white">
                                    No Keycloak user is signed in.
                                </h2>
                            </div>

                            <a
                                className="inline-flex w-fit items-center rounded-md bg-emerald-400 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-emerald-300"
                                href="/auth/keycloak/login"
                            >
                                Sign in with Keycloak
                            </a>

                            <div className="rounded-md bg-zinc-950 p-4 text-sm text-zinc-300">
                                <p className="font-medium text-zinc-100">Demo credentials</p>
                                <p className="mt-2 font-mono">lukas.student@example.com / Tutormatch123!</p>
                                <p className="mt-1 font-mono">anna.tutor@example.com / Tutormatch123!</p>
                                <p className="mt-1 font-mono">admin.tutormatch@example.com / Tutormatch123!</p>
                            </div>
                        </div>
                    )}
                </section>
            </div>
        </main>
    );
}
