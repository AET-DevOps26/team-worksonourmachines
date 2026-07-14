import { Form, Link, redirect, useLoaderData, useNavigation } from 'react-router';
import type { SharedStudentGeneratedPlanSuggestion } from '~/.server/api/server-student/generated';
import { isErr } from '~/.server/lib/result';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { generatePlan, getPlan } from '~/.server/service/student';
import { Badge } from '~/components/ui/badge';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

export const loader = protectedLoader(async ({ params }) => {
    const goalId = params.id ?? '';
    const result = await getPlan(goalId);
    if (isErr(result)) {
        if (result.error.type === 'notFound') {
            return { goalId, plan: null };
        }
        throw result.error;
    }
    return { goalId, plan: result.value };
});

export const action = protectedAction(async ({ params }) => {
    const goalId = params.id ?? '';
    const result = await generatePlan(goalId);
    if (isErr(result)) throw result.error;
    return redirect(`/plans/${goalId}`);
});

type PlanSuggestion = SharedStudentGeneratedPlanSuggestion;

const TIER_LABELS: Record<string, string> = {
    best_quality: 'Best quality',
    cheapest: 'Most affordable',
    within_budget: 'Within budget',
};

const TIER_BADGE_VARIANTS: Record<string, 'success' | 'warning' | 'default'> = {
    best_quality: 'default',
    cheapest: 'success',
    within_budget: 'warning',
};

export default function StudyPlanRoute() {
    const { plan, goalId } = useLoaderData<typeof loader>();
    const navigation = useNavigation();
    const isGenerating = navigation.state !== 'idle';

    if (!plan) {
        return (
            <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
                <div>
                    <h1 className="text-2xl font-semibold tracking-tight">Study plan</h1>
                    <p className="mt-1 text-sm text-muted-foreground">No plan has been generated for this goal yet.</p>
                </div>
                <Form method="post">
                    <Button disabled={isGenerating} type="submit">
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
                                Generating — this can take a couple of minutes…
                            </span>
                        ) : (
                            'Generate study plan'
                        )}
                    </Button>
                </Form>
                <Link
                    className={cn(buttonVariants({ size: 'sm', variant: 'outline' }), 'self-start')}
                    to={`/me/goals/${goalId}`}
                >
                    ← Back to goal
                </Link>
            </div>
        );
    }

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-semibold tracking-tight">Study plan suggestions</h1>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Three AI-generated options for your learning goal. Pick one and reach out to a tutor to get
                        started.
                    </p>
                </div>
                <Form method="post">
                    <Button disabled={isGenerating} size="sm" type="submit" variant="outline">
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
                            'Regenerate'
                        )}
                    </Button>
                </Form>
            </div>

            {plan.suggestions.map((suggestion) => (
                <SuggestionCard key={suggestion.tier} suggestion={suggestion} />
            ))}

            <p className="border-t pt-4 text-xs text-muted-foreground">
                These study plans were generated by an AI model and are provided for guidance only. Tutor availability,
                rates, and topic coverage may differ. Always verify details directly with the tutor.
            </p>
        </div>
    );
}

function SuggestionCard({ suggestion }: { suggestion: PlanSuggestion }) {
    const tier = suggestion.tier as string;
    return (
        <Card className="flex flex-col gap-4">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <div className="flex items-center gap-2">
                        <CardTitle>{TIER_LABELS[tier] ?? tier}</CardTitle>
                        <Badge variant={TIER_BADGE_VARIANTS[tier] ?? 'outline'}>{tier.replace('_', ' ')}</Badge>
                    </div>
                    <CardDescription className="mt-1">{suggestion.description}</CardDescription>
                </div>
                <div className="shrink-0 text-right">
                    <p className="text-xl font-semibold">€{suggestion.totalEstimatedCost}</p>
                    <p className="text-xs text-muted-foreground">estimated total</p>
                </div>
            </div>

            {suggestion.proposedTutors.length > 0 && (
                <div>
                    <p className="mb-2 text-sm font-medium">Proposed tutors</p>
                    <div className="flex flex-wrap gap-2">
                        {suggestion.proposedTutors.map((tutor) => (
                            <Badge key={tutor.id} variant="outline">
                                {tutor.displayName} · €{tutor.hourlyRate}/h
                            </Badge>
                        ))}
                    </div>
                </div>
            )}

            {suggestion.milestones.length > 0 && (
                <div>
                    <p className="mb-2 text-sm font-medium">Milestones</p>
                    <ol className="flex flex-col gap-2">
                        {suggestion.milestones.map((milestone, i) => (
                            <li className="flex items-start gap-3 text-sm" key={milestone.title}>
                                <span className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
                                    {i + 1}
                                </span>
                                <div className="flex-1">
                                    <span className="font-medium">{milestone.title}</span>
                                    <span className="ml-2 text-muted-foreground">
                                        due {new Date(milestone.dueDate).toLocaleDateString('en-GB')} · €
                                        {milestone.estimatedCost}
                                    </span>
                                </div>
                            </li>
                        ))}
                    </ol>
                </div>
            )}
        </Card>
    );
}
