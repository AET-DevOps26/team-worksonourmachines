import { useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { generatePlan, type PlanSuggestion } from '~/.server/service/ai';
import { protectedLoader } from '~/.server/service/routeProtection';
import { Badge } from '~/components/ui/badge';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';

export const loader = protectedLoader(async ({ params }) => {
    const goalId = params.id ?? '';
    const result = await generatePlan(goalId);
    if (isErr(result)) throw result.error;
    return { plan: result.value };
});

const TIER_LABELS: Record<PlanSuggestion['tier'], string> = {
    best_quality: 'Best quality',
    cheapest: 'Most affordable',
    within_budget: 'Within budget',
};

const TIER_BADGE_VARIANTS: Record<PlanSuggestion['tier'], 'success' | 'warning' | 'default'> = {
    best_quality: 'default',
    cheapest: 'success',
    within_budget: 'warning',
};

export default function StudyPlanRoute() {
    const { plan } = useLoaderData<typeof loader>();

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
            <div>
                <h1 className="text-2xl font-semibold tracking-tight">Study plan suggestions</h1>
                <p className="mt-1 text-sm text-muted-foreground">
                    Three AI-generated options for your learning goal. Pick one and reach out to a tutor to get started.
                </p>
            </div>

            {plan.suggestions.map((suggestion) => (
                <SuggestionCard key={suggestion.tier} suggestion={suggestion} />
            ))}
        </div>
    );
}

function SuggestionCard({ suggestion }: { suggestion: PlanSuggestion }) {
    return (
        <Card className="flex flex-col gap-4">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <div className="flex items-center gap-2">
                        <CardTitle>{TIER_LABELS[suggestion.tier]}</CardTitle>
                        <Badge variant={TIER_BADGE_VARIANTS[suggestion.tier]}>
                            {suggestion.tier.replace('_', ' ')}
                        </Badge>
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
                                        due {new Date(milestone.dueDate).toLocaleDateString()} · €
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
