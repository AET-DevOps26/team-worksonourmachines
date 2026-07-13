import { Form, Link, redirect, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listModules } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { deleteGoal, getGoal } from '~/.server/service/student';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

const LEVEL_LABELS: Record<number, string> = {
    1: 'Beginner',
    2: 'Elementary',
    3: 'Intermediate',
    4: 'Advanced',
    5: 'Expert',
};

export const loader = protectedLoader(async ({ params }) => {
    const id = params.id ?? '';
    const [goalResult, modulesResult] = await Promise.all([getGoal(id), listModules({ pageSize: 100 })]);
    if (isErr(goalResult)) throw goalResult.error;
    const moduleCode = isErr(modulesResult)
        ? undefined
        : modulesResult.value.items.find((m) => m.id === goalResult.value.moduleId)?.code;
    return { goal: goalResult.value, moduleCode };
});

export const action = protectedAction(async ({ request, params }) => {
    const id = params.id ?? '';
    const formData = await request.formData();
    const _action = formData.get('_action');

    if (_action === 'delete') {
        const result = await deleteGoal(id);
        if (isErr(result)) {
            return { error: 'Could not delete goal. Please try again.' };
        }
        return redirect('/me/goals');
    }

    return { error: 'Unknown action.' };
});

export default function LearningGoalDetailRoute() {
    const { goal, moduleCode } = useLoaderData<typeof loader>();
    const navigation = useNavigation();
    const isGenerating = navigation.state === 'loading' && navigation.location?.pathname === `/plans/${goal.id}`;

    return (
        <div className="mx-auto flex w-full max-w-2xl flex-col gap-6">
            <Card>
                <div className="flex items-start justify-between gap-4">
                    <div>
                        <CardTitle>{goal.description}</CardTitle>
                        <CardDescription className="mt-1">Module: {moduleCode ?? goal.moduleId}</CardDescription>
                    </div>
                    <Link
                        className={cn(
                            buttonVariants({ size: 'sm' }),
                            isGenerating ? 'pointer-events-none opacity-60' : '',
                        )}
                        to={`/plans/${goal.id}`}
                    >
                        {isGenerating ? (
                            <span className="flex items-center gap-1.5">
                                <svg
                                    className="h-3.5 w-3.5 animate-spin"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg"
                                >
                                    <circle
                                        className="opacity-25"
                                        cx="12"
                                        cy="12"
                                        r="10"
                                        stroke="currentColor"
                                        strokeWidth="4"
                                    />
                                    <path
                                        className="opacity-75"
                                        d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                                        fill="currentColor"
                                    />
                                </svg>
                                Generating…
                            </span>
                        ) : (
                            'Generate study plan'
                        )}
                    </Link>
                </div>
                {isGenerating && (
                    <p className="mt-3 text-sm text-muted-foreground">
                        Generating your personalised study plan — this can take a couple of minutes.
                    </p>
                )}
            </Card>

            <Card className="flex flex-col gap-4">
                <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Target date</p>
                    <p className="mt-1 text-sm">{new Date(goal.targetDate).toLocaleDateString()}</p>
                </div>
                <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Current level</p>
                    <p className="mt-1 text-sm">
                        {goal.selfAssessedLevel} — {LEVEL_LABELS[goal.selfAssessedLevel] ?? ''}
                    </p>
                </div>
                {goal.budgetEur != null && (
                    <div>
                        <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Budget</p>
                        <p className="mt-1 text-sm">€{goal.budgetEur}</p>
                    </div>
                )}
                {goal.locations.length > 0 && (
                    <div>
                        <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Locations</p>
                        <p className="mt-1 text-sm capitalize">{goal.locations.join(', ')}</p>
                    </div>
                )}
            </Card>

            <div className="flex items-center justify-between gap-4">
                <Link className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))} to="/me/goals">
                    ← Back to goals
                </Link>
                <Form method="post">
                    <input name="_action" type="hidden" value="delete" />
                    <Button size="sm" type="submit" variant="destructive">
                        Delete goal
                    </Button>
                </Form>
            </div>
        </div>
    );
}
