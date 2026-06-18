import type { LoaderFunctionArgs } from 'react-router';
import { Outlet, useLoaderData } from 'react-router';
import { getAuthenticatedUser } from '~/.server/service/keycloak';
import { AppShell } from '~/components/shell';

export async function loader({ request }: LoaderFunctionArgs) {
    return {
        user: await getAuthenticatedUser(request),
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
