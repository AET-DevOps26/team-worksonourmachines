import { useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import { getModule } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { Badge } from '~/components/ui/badge';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';

export const loader = protectedLoader(async ({ params }) => {
    const code = params.code ?? '';
    const moduleResult = await getModule(code);
    if (isErr(moduleResult)) {
        throwRouteError(moduleResult.error);
    }
    return { module: moduleResult.value };
});

export default function ModuleDetailRoute() {
    const { module } = useLoaderData<typeof loader>();

    return (
        <PageContainer className="flex flex-col gap-6" size="wide">
            <Card>
                <div className="flex items-center gap-3">
                    <CardTitle>{module.code}</CardTitle>
                    <Badge variant="outline">{module.difficultyHint}</Badge>
                </div>
                <CardDescription className="mt-2 text-base text-foreground">{module.title}</CardDescription>
                <p className="mt-3 text-sm text-muted-foreground">{module.description}</p>
            </Card>

            <section className="flex flex-col gap-4">
                <h2 className="text-lg font-semibold">Topics</h2>
                <div className="grid gap-3 sm:grid-cols-2">
                    {module.topics.map((topic) => (
                        <Card key={topic.id}>
                            <div className="flex items-center justify-between gap-2">
                                <CardTitle className="text-base">{topic.name}</CardTitle>
                                <Badge
                                    variant={
                                        topic.difficultyHint === 'Hard'
                                            ? 'danger'
                                            : topic.difficultyHint === 'Easy'
                                              ? 'success'
                                              : 'warning'
                                    }
                                >
                                    {topic.difficultyHint}
                                </Badge>
                            </div>
                            <CardDescription className="mt-2">{topic.description}</CardDescription>
                        </Card>
                    ))}
                </div>
            </section>
        </PageContainer>
    );
}
