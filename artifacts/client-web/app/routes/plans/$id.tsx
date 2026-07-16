import { useEffect, useRef, useState } from 'react';
import { Link, useFetcher, useLoaderData, useRevalidator } from 'react-router';
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
    if (isErr(result)) {
        const detail = result.error.detail;
        if (result.error.type === 'badRequest' && detail) {
            return { error: detail, ok: false };
        }
        return { error: 'Generation failed. Please try again.', ok: false };
    }
    return { error: null, ok: true };
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

function generatingKey(goalId: string) {
    return `plan-generating:${goalId}`;
}

function regeneratingFromKey(goalId: string) {
    return `plan-regenerating-from:${goalId}`;
}
export default function StudyPlanRoute() {
    const { plan, goalId } = useLoaderData<typeof loader>();
    const fetcher = useFetcher();
    const { revalidate } = useRevalidator();
    const pollRef = useRef<ReturnType<typeof setInterval> | null>(null);

    const isSubmitting = fetcher.state !== 'idle';
    const actionError = fetcher.data && !fetcher.data.ok ? fetcher.data.error : null;

    const [hydrated, setHydrated] = useState(false);
    const [storedGenerating, setStoredGenerating] = useState(false);
    const [regeneratingFromId, setRegeneratingFromId] = useState<string | null>(null);

    useEffect(() => {
        setStoredGenerating(localStorage.getItem(generatingKey(goalId)) === '1');
        setRegeneratingFromId(localStorage.getItem(regeneratingFromKey(goalId)));
        setHydrated(true);
    }, [goalId]); // eslint-disable-line react-hooks/exhaustive-deps

    const isGenerating = isSubmitting || storedGenerating;
    // Before hydration we don't know whether we're generating — hide the button
    // so the user never sees a stale "Regenerate" label before localStorage is read.
    const buttonReady = hydrated || isSubmitting;

    // When fetcher submits, mark generating and record the current plan id so we
    // can detect when a genuinely new plan has arrived after a regenerate.
    useEffect(() => {
        if (fetcher.state === 'submitting') {
            localStorage.setItem(generatingKey(goalId), '1');
            setStoredGenerating(true);
            const fromId = plan?.id ?? null;
            if (fromId) {
                localStorage.setItem(regeneratingFromKey(goalId), fromId);
            } else {
                localStorage.removeItem(regeneratingFromKey(goalId));
            }
            setRegeneratingFromId(fromId);
        }
        // When the action completes with an error, clear the generating flag so
        // the button re-enables and the user can try again.
        if (fetcher.state === 'idle' && fetcher.data && !fetcher.data.ok) {
            localStorage.removeItem(generatingKey(goalId));
            localStorage.removeItem(regeneratingFromKey(goalId));
            setStoredGenerating(false);
            setRegeneratingFromId(null);
        }
    }, [fetcher.state, fetcher.data, goalId, plan?.id]);

    function handleGenerateClick() {
        // intentionally empty — state is set in the fetcher.state effect above
        // to avoid unmounting the form before the submit fires
    }

    // Poll every 4 seconds while generating.
    useEffect(() => {
        if (isGenerating) {
            pollRef.current = setInterval(() => revalidate(), 4000);
        }
        return () => {
            if (pollRef.current) {
                clearInterval(pollRef.current);
                pollRef.current = null;
            }
        };
    }, [isGenerating, revalidate]);

    // Clear generating flag when the expected new plan arrives.
    // For regenerate: wait until plan.id differs from the stored id.
    // For first generate: any plan arriving is enough.
    useEffect(() => {
        if (!isGenerating) return;
        const isNewPlan = regeneratingFromId
            ? plan !== null && plan !== undefined && plan.id !== regeneratingFromId
            : !!plan;
        if (isNewPlan) {
            localStorage.removeItem(generatingKey(goalId));
            localStorage.removeItem(regeneratingFromKey(goalId));
            setStoredGenerating(false);
            setRegeneratingFromId(null);
            if (pollRef.current) {
                clearInterval(pollRef.current);
                pollRef.current = null;
            }
        }
    }, [plan, goalId, isGenerating, regeneratingFromId]);

    // Safety timeout: clear the generating flag after 10 minutes so the UI never
    // gets permanently stuck if the server request failed silently.
    useEffect(() => {
        if (!isGenerating) return;
        const timeout = setTimeout(
            () => {
                localStorage.removeItem(generatingKey(goalId));
                localStorage.removeItem(regeneratingFromKey(goalId));
                setStoredGenerating(false);
                setRegeneratingFromId(null);
            },
            10 * 60 * 1000,
        );
        return () => clearTimeout(timeout);
    }, [isGenerating, goalId]);

    const spinner = (
        <svg className="h-3.5 w-3.5 animate-spin" fill="none" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" fill="currentColor" />
        </svg>
    );

    if (!plan) {
        return (
            <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
                <div>
                    <h1 className="text-2xl font-semibold tracking-tight">Study plan</h1>
                    <p className="mt-1 text-sm text-muted-foreground">
                        {isGenerating
                            ? "Generating your study plan — this can take a couple of minutes. You can navigate away; we'll keep checking."
                            : 'No plan has been generated for this goal yet.'}
                    </p>
                </div>
                {actionError && <p className="text-sm text-destructive">{actionError}</p>}
                {!isGenerating && (
                    <fetcher.Form method="post">
                        <Button onClick={handleGenerateClick} type="submit">
                            Generate study plan
                        </Button>
                    </fetcher.Form>
                )}
                {isGenerating && (
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                        {spinner}
                        <span>Checking for your plan…</span>
                    </div>
                )}
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
                    {actionError && <p className="mt-1 text-sm text-destructive">{actionError}</p>}
                </div>
                <fetcher.Form method="post">
                    {buttonReady && (
                        <Button
                            disabled={isGenerating}
                            onClick={handleGenerateClick}
                            size="sm"
                            type="submit"
                            variant="outline"
                        >
                            {isGenerating ? (
                                <span className="flex items-center gap-1.5">
                                    {spinner}
                                    Regenerating…
                                </span>
                            ) : (
                                'Regenerate'
                            )}
                        </Button>
                    )}
                </fetcher.Form>
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
    const tutorById = Object.fromEntries(suggestion.proposedTutors.map((t) => [t.id, t]));
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
                            <li className="flex items-start gap-3 text-sm" key={milestone.topicId}>
                                <span className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
                                    {i + 1}
                                </span>
                                <div className="flex-1">
                                    <span className="font-medium">{milestone.title}</span>
                                    <span className="ml-2 text-muted-foreground">
                                        due {new Date(milestone.dueDate).toLocaleDateString('en-GB')} · €
                                        {milestone.estimatedCost}
                                        {tutorById[milestone.tutorId] && (
                                            <> · {tutorById[milestone.tutorId]!.displayName}</>
                                        )}
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
