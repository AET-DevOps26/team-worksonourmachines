import type { LoaderFunctionArgs } from 'react-router';
import { Outlet, useLoaderData } from 'react-router';
import { logger } from '~/.server/lib/logger';
import { getSessionUser, type SessionUser } from '~/.server/service/session';
import { RouteErrorPage } from '~/components/errors';
import { AppShell, type ShellUser } from '~/components/shell';

function toShellUser(user: SessionUser): ShellUser {
    return {
        sub: user.sub,
        username: user.preferredUsername ?? user.email ?? user.sub,
        ...(user.email ? { email: user.email } : {}),
        ...(user.name ? { name: user.name } : {}),
        roles: [...user.roles],
    };
}

export async function loader({ request }: LoaderFunctionArgs) {
    const userResult = await getSessionUser(request);
    if (userResult.isErr) {
        logger.error('Failed to load session user in app layout', { error: userResult.error });
    }

    return {
        user: userResult.isOk && userResult.value ? toShellUser(userResult.value) : null,
    };
}

export default function AppLayout() {
    const { user } = useLoaderData<typeof loader>();

    return (
        <AppShell user={user}>
            <Outlet />
        </AppShell>
    );
}

export function ErrorBoundary() {
    const data = useLoaderData<typeof loader>() as { user: ShellUser | null } | undefined; // manual type assignment as the loader could break and therefore be undefined
    const user = data?.user ?? null;
    return (
        <AppShell user={user}>
            <RouteErrorPage />
        </AppShell>
    );
}
