import type { LoaderFunctionArgs } from 'react-router';
import { useLoaderData, useRouteLoaderData } from 'react-router';
import type { ShellUser } from '~/components/shell';

export async function loader({ request }: LoaderFunctionArgs) {
    const requestUrl = new URL(request.url);

    return {
        authError: requestUrl.searchParams.get('auth_error'),
    };
}

type AppLayoutLoaderData = {
    user: ShellUser | null;
};

export default function HomeRoute() {
    const { authError } = useLoaderData<typeof loader>();
    const layoutData = useRouteLoaderData('routes/app-layout') as AppLayoutLoaderData | undefined;
    const user = layoutData?.user;

    return (
        <div className="mx-auto flex w-full max-w-4xl flex-col gap-8 px-6 py-16">
            {authError ? (
                <div className="rounded-md border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                    {authError}
                </div>
            ) : null}

            <div className="rounded-md border border-green-400/40 bg-green-100 px-4 py-3 text-sm text-green-700">
                {user ? (
                    `Welcome back, ${user.name ?? user.username}.`
                ) : (
                    <div>
                        <p className="font-medium text-black">Demo credentials</p>
                        <p className="mt-2 font-mono">lukas.student@example.com / Tutormatch123!</p>
                        <p className="mt-1 font-mono">anna.tutor@example.com / Tutormatch123!</p>
                        <p className="mt-1 font-mono">admin.tutormatch@example.com / Tutormatch123!</p>
                    </div>
                )}
            </div>

            <section className="flex flex-col gap-4">
                Lorem ipsum dolor sit amet consectetur adipisicing elit. Minima aliquam dolorum nam inventore eligendi
                voluptatum amet? Quasi amet hic blanditiis molestiae velit eius aliquid nisi, voluptatem, atque,
                exercitationem pariatur tempore.
            </section>
        </div>
    );
}
