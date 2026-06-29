import { Link, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listAdminModules } from '~/.server/service/marketplace';
import { roleProtectedLoader } from '~/.server/service/routeProtection';
import { Badge } from '~/components/ui/badge';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

const adminModulesPageClassName = 'mx-auto flex w-full max-w-3xl flex-col gap-6';

export const loader = roleProtectedLoader('admin', async () => {
    const result = await listAdminModules();
    if (isErr(result)) throw result.error;
    return { modules: result.value };
});

export default function AdminModulesRoute() {
    const { modules } = useLoaderData<typeof loader>();

    return (
        <div className={adminModulesPageClassName}>
            <Card>
                <div className="flex items-start justify-between gap-4">
                    <div className="flex flex-col gap-1">
                        <CardTitle>Manage modules</CardTitle>
                        <CardDescription>Create and manage modules, topics, and difficulty hints.</CardDescription>
                    </div>
                    <Link className={cn(buttonVariants({ size: 'sm' }))} to="/admin/modules/new">
                        Create module
                    </Link>
                </div>
            </Card>

            {modules.length === 0 ? (
                <Card>
                    <CardDescription>No modules yet. Create the first module to get started.</CardDescription>
                </Card>
            ) : (
                <div className="flex flex-col gap-3">
                    {modules.map((mod) => (
                        <Card key={mod.id}>
                            <div className="flex items-start justify-between gap-4">
                                <div className="flex min-w-0 flex-col gap-2">
                                    <div className="flex flex-wrap items-center gap-2">
                                        <CardTitle className="text-base">{mod.code}</CardTitle>
                                        <Badge variant="outline">{mod.difficultyHint}</Badge>
                                    </div>
                                    <CardDescription>{mod.title}</CardDescription>
                                    <p className="line-clamp-2 text-sm text-muted-foreground">{mod.description}</p>
                                    <p className="text-xs text-muted-foreground">
                                        {mod.topics.length} topic{mod.topics.length === 1 ? '' : 's'}
                                    </p>
                                </div>
                                <Link
                                    className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))}
                                    to={`/admin/modules/${mod.code}`}
                                >
                                    Edit
                                </Link>
                            </div>
                        </Card>
                    ))}
                </div>
            )}
        </div>
    );
}
