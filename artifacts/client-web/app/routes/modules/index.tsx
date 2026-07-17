import { Link, useLoaderData, useSearchParams } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import { listModules } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { Badge } from '~/components/ui/badge';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';

export const loader = protectedLoader(async ({ request }) => {
    const url = new URL(request.url);
    const q = url.searchParams.get('q') ?? undefined;
    const page = Number(url.searchParams.get('page') ?? 1);
    const moduleParams: Parameters<typeof listModules>[0] = { page, pageSize: 20 };
    if (q) moduleParams.q = q;
    const result = await listModules(moduleParams);
    if (isErr(result)) {
        throwRouteError(result.error);
    }
    return { modules: result.value };
});

export default function ModulesIndexRoute() {
    const { modules } = useLoaderData<typeof loader>();
    const [searchParams] = useSearchParams();
    const q = searchParams.get('q') ?? '';

    return (
        <PageContainer className="flex flex-col gap-6" size="wide">
            <Card>
                <CardTitle>Course modules</CardTitle>
                <CardDescription>
                    Browse modules and explore topic difficulty hints before choosing a tutor.
                </CardDescription>
                <form className="mt-4 flex gap-2" method="get">
                    <Input defaultValue={q} name="q" placeholder="Search modules…" type="search" />
                    <Button type="submit">Search</Button>
                </form>
            </Card>
            <div className="grid gap-4 sm:grid-cols-2">
                {modules.items.map((module) => (
                    <Link key={module.id} to={`/modules/${module.code}`}>
                        <Card className="transition-colors hover:border-primary/40">
                            <div className="flex items-center justify-between gap-2">
                                <CardTitle>{module.code}</CardTitle>
                                <Badge variant="outline">{module.difficultyHint}</Badge>
                            </div>
                            <CardDescription className="mt-2">{module.title}</CardDescription>
                            <p className="mt-2 line-clamp-2 text-sm text-muted-foreground">{module.description}</p>
                        </Card>
                    </Link>
                ))}
            </div>
        </PageContainer>
    );
}
