import { Link, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listModules } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { listMyGoals } from '~/.server/service/student';
import { PageContainer } from '~/components/shell';
import { Badge } from '~/components/ui/badge';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

export const loader = protectedLoader(async () => {
    const [goalsResult, modulesResult] = await Promise.all([listMyGoals(), listModules({ pageSize: 100 })]);
    if (isErr(goalsResult)) throw goalsResult.error;
    if (isErr(modulesResult)) throw modulesResult.error;
    const moduleCodeById = Object.fromEntries(modulesResult.value.items.map((m) => [m.id, m.code]));
    return { goals: goalsResult.value, moduleCodeById };
});

const LEVEL_LABELS: Record<number, string> = {
    1: 'Beginner',
    2: 'Elementary',
    3: 'Intermediate',
    4: 'Advanced',
    5: 'Expert',
};

export default function LearningGoalsRoute() {
    const { goals, moduleCodeById } = useLoaderData<typeof loader>();

    return (
        <PageContainer className="flex flex-col gap-6" size="wide">
            <Card>
                <div className="flex items-center justify-between gap-4">
                    <div>
                        <CardTitle>My learning goals</CardTitle>
                        <CardDescription className="mt-1">
                            Your learning goals across the modules you are studying.
                        </CardDescription>
                    </div>
                    <Link className={cn(buttonVariants({ size: 'sm' }))} to="/me/goals/new">
                        New goal
                    </Link>
                </div>
            </Card>

            {goals.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                    No goals yet.{' '}
                    <Link className="underline" to="/me/goals/new">
                        Create your first goal.
                    </Link>
                </p>
            ) : (
                <div className="grid gap-4 sm:grid-cols-2">
                    {goals.map((goal) => (
                        <Link key={goal.id} to={`/me/goals/${goal.id}`}>
                            <Card className="transition-colors hover:border-primary/40">
                                <div className="flex items-start justify-between gap-2">
                                    <CardTitle className="line-clamp-2">{goal.description}</CardTitle>
                                    <Badge variant="outline">
                                        {LEVEL_LABELS[goal.selfAssessedLevel] ?? goal.selfAssessedLevel}
                                    </Badge>
                                </div>
                                <CardDescription className="mt-2">
                                    Module: {moduleCodeById[goal.moduleId] ?? goal.moduleId}
                                </CardDescription>
                                <p className="mt-1 text-sm text-muted-foreground">
                                    Target: {new Date(goal.targetDate).toLocaleDateString()}
                                    {goal.budgetEur != null && ` · Budget: €${goal.budgetEur}`}
                                </p>
                            </Card>
                        </Link>
                    ))}
                </div>
            )}
        </PageContainer>
    );
}
