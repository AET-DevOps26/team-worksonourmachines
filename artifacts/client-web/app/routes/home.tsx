import type { LoaderFunctionArgs } from 'react-router';
import { Form, Link, useLoaderData } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { getSessionUser } from '~/.server/service/session';

export async function loader({ request }: LoaderFunctionArgs) {
    const userResult = await getSessionUser(request);
    if (userResult.isErr) {
        logger.error('Failed to load session user on home page', { error: userResult.error });
    }

    return {
        user: userResult.isOk ? userResult.value : null,
    };
}

export default function HomeRoute() {
    const { user } = useLoaderData<typeof loader>();

    return (
        <main className="min-h-svh bg-zinc-950 px-6 py-10 text-zinc-100">
            <div className="mx-auto flex w-full max-w-4xl flex-col gap-8">
                <header className="flex flex-col gap-3">
                    <p className="text-sm font-medium uppercase tracking-wide text-emerald-300">TUtorMatch</p>
                    <h1 className="text-3xl font-semibold tracking-normal text-white">Welcome</h1>
                    <p className="max-w-2xl text-base leading-7 text-zinc-300">
                        The TUtorMatch web client uses Keycloak for authentication through a secure
                        backend-for-frontend.
                    </p>
                </header>

                <section className="rounded-md border border-zinc-800 bg-zinc-900 p-6 shadow-xl shadow-black/20">
                    {user ? (
                        <div className="flex flex-col gap-5">
                            <div>
                                <p className="text-sm font-medium text-emerald-300">Signed in</p>
                                <h2 className="mt-1 text-2xl font-semibold text-white">
                                    {user.name ?? user.preferredUsername ?? user.email}
                                </h2>
                                <p className="mt-1 text-sm text-zinc-400">{user.email ?? user.preferredUsername}</p>
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

                            <Form action="/auth/logout" method="post">
                                <button
                                    className="inline-flex w-fit items-center rounded-md bg-zinc-100 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-white"
                                    type="submit"
                                >
                                    Sign out
                                </button>
                            </Form>
                        </div>
                    ) : (
                        <div className="flex flex-col gap-5">
                            <div>
                                <p className="text-sm font-medium text-zinc-400">Anonymous session</p>
                                <h2 className="mt-1 text-2xl font-semibold text-white">You are not signed in.</h2>
                            </div>

                            <Link
                                className="inline-flex w-fit items-center rounded-md bg-emerald-400 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-emerald-300"
                                to="/login"
                            >
                                Sign in
                            </Link>
                        </div>
                    )}
                </section>
            </div>
        </main>
    );
}
