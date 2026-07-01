import { Link, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listAdminModules, listAdminTutorApplications } from '~/.server/service/marketplace';
import { roleProtectedLoader } from '~/.server/service/routeProtection';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

export const loader = roleProtectedLoader('admin', async () => {
    const [appsResult, modulesResult] = await Promise.all([listAdminTutorApplications('pending'), listAdminModules()]);
    if (isErr(appsResult)) throw appsResult.error;
    if (isErr(modulesResult)) throw modulesResult.error;
    return {
        moduleCount: modulesResult.value.length,
        pendingCount: appsResult.value.length,
    };
});

export default function AdminDashboardRoute() {
    const { moduleCount, pendingCount } = useLoaderData<typeof loader>();

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
            <Card>
                <CardTitle>Admin dashboard</CardTitle>
                <CardDescription>Manage tutor approvals and course modules.</CardDescription>
            </Card>
            <div className="grid gap-4 sm:grid-cols-2">
                <Card>
                    <CardTitle className="text-base">Pending approvals</CardTitle>
                    <p className="mt-2 text-3xl font-semibold">{pendingCount}</p>
                    <Link className={cn(buttonVariants({ className: 'mt-4', size: 'sm' }))} to="/admin/tutor-approvals">
                        Review applications
                    </Link>
                </Card>
                <Card>
                    <CardTitle className="text-base">Modules</CardTitle>
                    <p className="mt-2 text-3xl font-semibold">{moduleCount}</p>
                    <Link
                        className={cn(buttonVariants({ className: 'mt-4', size: 'sm', variant: 'outline' }))}
                        to="/admin/modules"
                    >
                        Manage modules
                    </Link>
                </Card>
            </div>
        </div>
    );
}
