import { useId, useState } from 'react';
import { Button } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { cn } from '~/lib/ui/utils';

// ---------------------------------------------------------------------------
// Static mock data — stand-ins for real API data
// ---------------------------------------------------------------------------

const MOCK_TOPICS = [
    { difficultyLevel: 2, id: 't1', name: 'Linear Systems & Gaussian Elimination' },
    { difficultyLevel: 4, id: 't2', name: 'Matrix Decomposition (LU, QR, SVD)' },
    { difficultyLevel: 4, id: 't3', name: 'Eigenvalues & Eigenvectors' },
    { difficultyLevel: 3, id: 't4', name: 'Vector Spaces & Subspaces' },
    { difficultyLevel: 3, id: 't5', name: 'Inner Products & Orthogonality' },
    { difficultyLevel: 5, id: 't6', name: 'Numerical Stability & Conditioning' },
];

const MOCK_TUTORS = [
    {
        hourlyRate: 15,
        id: 'u1',
        languages: ['de', 'en'],
        name: 'Anna Schmidt',
        rating: 4.8,
        sessions: 42,
        topicIds: ['t1', 't2', 't4'],
    },
    {
        hourlyRate: 20,
        id: 'u2',
        languages: ['en', 'es'],
        name: 'Carlos Fernández',
        rating: 4.9,
        sessions: 67,
        topicIds: ['t2', 't3', 't5', 't6'],
    },
    {
        hourlyRate: 12,
        id: 'u3',
        languages: ['en', 'zh'],
        name: 'Mei Lin',
        rating: 4.6,
        sessions: 29,
        topicIds: ['t1', 't3', 't4'],
    },
    {
        hourlyRate: 25,
        id: 'u4',
        languages: ['de', 'en'],
        name: 'Jonas Weber',
        rating: 5.0,
        sessions: 110,
        topicIds: ['t1', 't2', 't3', 't4', 't5', 't6'],
    },
    {
        hourlyRate: 18,
        id: 'u5',
        languages: ['en'],
        name: 'Priya Nair',
        rating: 4.7,
        sessions: 55,
        topicIds: ['t3', 't5', 't6'],
    },
];

// ---------------------------------------------------------------------------
// Plan generation — purely client-side mock (no API call)
// ---------------------------------------------------------------------------

type ScheduleEntry = {
    topicId: string;
    topicName: string;
    tutorId: string;
    tutorName: string;
    estimatedCost: number;
};

type SuggestedPlan = {
    tier: 'cheapest' | 'within_budget' | 'best_quality';
    label: string;
    tagline: string;
    schedule: ScheduleEntry[];
    totalCost: number;
    topicsCovered: number;
};

function buildPlan(
    topics: typeof MOCK_TOPICS,
    tutors: typeof MOCK_TUTORS,
    strategy: 'cheapest' | 'within_budget' | 'best_quality',
    budgetEur: number,
    selectedTopicIds: string[],
    preferredLanguage: string,
): SuggestedPlan {
    const targetTopics = topics.filter((t) => selectedTopicIds.includes(t.id));

    // language filter: keep tutors who speak the preferred language (or all if "any")
    const langFiltered =
        preferredLanguage === 'any' ? tutors : tutors.filter((t) => t.languages.includes(preferredLanguage));
    const pool = langFiltered.length > 0 ? langFiltered : tutors;

    const sorted = [...pool].sort((a, b) => {
        if (strategy === 'cheapest') return a.hourlyRate - b.hourlyRate;
        if (strategy === 'best_quality') return b.rating - a.rating || b.sessions - a.sessions;
        // within_budget: prefer best rating while staying cheap enough
        return a.hourlyRate - b.hourlyRate || b.rating - a.rating;
    });

    const schedule: ScheduleEntry[] = [];
    let totalCost = 0;

    for (const topic of targetTopics) {
        const eligible = sorted.filter((t) => t.topicIds.includes(topic.id));
        if (!eligible.length) continue;

        let chosen = eligible[0];
        if (!chosen) continue;

        if (strategy === 'within_budget') {
            // pick the highest-rated tutor whose cost still fits within the remaining budget
            const fits = eligible.filter((t) => totalCost + t.hourlyRate <= budgetEur);
            const bestFit = fits.reduce<(typeof MOCK_TUTORS)[number] | undefined>(
                (best, t) => (!best || t.rating > best.rating ? t : best),
                undefined,
            );
            if (bestFit) chosen = bestFit;
        }

        schedule.push({
            estimatedCost: chosen.hourlyRate,
            topicId: topic.id,
            topicName: topic.name,
            tutorId: chosen.id,
            tutorName: chosen.name,
        });
        totalCost += chosen.hourlyRate;
    }

    const labels: Record<SuggestedPlan['tier'], { label: string; tagline: string }> = {
        best_quality: {
            label: 'Best quality',
            tagline: 'Highest-rated tutors for every topic.',
        },
        cheapest: {
            label: 'Cheapest option',
            tagline: 'Lowest-cost tutors across all topics.',
        },
        within_budget: {
            label: 'Within budget',
            tagline: `Balanced plan that stays within €${budgetEur}.`,
        },
    };

    return {
        ...labels[strategy],
        schedule,
        tier: strategy,
        topicsCovered: schedule.length,
        totalCost,
    };
}

// ---------------------------------------------------------------------------
// Component helpers
// ---------------------------------------------------------------------------

const DIFFICULTY_LABELS: Record<number, string> = {
    1: 'Very easy',
    2: 'Easy',
    3: 'Medium',
    4: 'Hard',
    5: 'Very hard',
};

function DifficultyDots({ level }: { level: number }) {
    return (
        <span className="flex items-center gap-0.5">
            {[1, 2, 3, 4, 5].map((i) => (
                <span
                    className={cn('inline-block size-1.5 rounded-full', i <= level ? 'bg-primary' : 'bg-muted')}
                    key={i}
                />
            ))}
        </span>
    );
}

function TierBadge({ tier }: { tier: SuggestedPlan['tier'] }) {
    const styles: Record<SuggestedPlan['tier'], string> = {
        best_quality: 'bg-amber-500/15 text-amber-700 dark:text-amber-300',
        cheapest: 'bg-emerald-500/15 text-emerald-700 dark:text-emerald-300',
        within_budget: 'bg-blue-500/15 text-blue-700 dark:text-blue-300',
    };
    return (
        <span className={cn('rounded-full px-2.5 py-0.5 text-xs font-semibold', styles[tier])}>
            {tier === 'cheapest' && '💸 Cheapest'}
            {tier === 'within_budget' && '✅ Within budget'}
            {tier === 'best_quality' && '⭐ Best quality'}
        </span>
    );
}

function PlanCard({ plan }: { plan: SuggestedPlan }) {
    const [expanded, setExpanded] = useState(false);

    return (
        <div className="flex flex-col gap-4 rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-start justify-between gap-3">
                <div className="flex flex-col gap-1">
                    <TierBadge tier={plan.tier} />
                    <h3 className="mt-2 text-base font-semibold text-foreground">{plan.label}</h3>
                    <p className="text-sm text-muted-foreground">{plan.tagline}</p>
                </div>
                <div className="flex flex-col items-end gap-1 text-right">
                    <span className="text-2xl font-bold text-foreground">€{plan.totalCost.toFixed(2)}</span>
                    <span className="text-xs text-muted-foreground">
                        {plan.topicsCovered} topic{plan.topicsCovered !== 1 ? 's' : ''} covered
                    </span>
                </div>
            </div>

            <button
                className="flex items-center gap-1 self-start text-xs font-medium text-primary underline-offset-2 hover:underline"
                onClick={() => setExpanded((v) => !v)}
                type="button"
            >
                {expanded ? 'Hide sessions ▲' : 'Show sessions ▼'}
            </button>

            {expanded && (
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-border text-left text-xs font-medium text-muted-foreground">
                            <th className="pb-2 pr-4">Topic</th>
                            <th className="pb-2 pr-4">Tutor</th>
                            <th className="pb-2 text-right">Cost / session</th>
                        </tr>
                    </thead>
                    <tbody>
                        {plan.schedule.map((entry) => (
                            <tr className="border-b border-border/60 last:border-0" key={entry.topicId}>
                                <td className="py-2 pr-4 text-foreground">{entry.topicName}</td>
                                <td className="py-2 pr-4 text-muted-foreground">{entry.tutorName}</td>
                                <td className="py-2 text-right font-mono text-foreground">
                                    €{entry.estimatedCost.toFixed(2)}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            <Button className="mt-1 w-full" size="sm" variant="outline">
                Browse tutors in this plan
            </Button>
        </div>
    );
}

// ---------------------------------------------------------------------------
// Main route
// ---------------------------------------------------------------------------

export default function NewPlanRoute() {
    const [courseTopic, setCourseTopic] = useState('Linear Algebra (MA0901)');
    const [budget, setBudget] = useState('80');
    const [preferredLanguage, setPreferredLanguage] = useState('any');
    const [selectedTopicIds, setSelectedTopicIds] = useState<string[]>(MOCK_TOPICS.map((t) => t.id));
    const [selectedTutorIds, setSelectedTutorIds] = useState<string[]>(MOCK_TUTORS.map((t) => t.id));
    const [plans, setPlans] = useState<SuggestedPlan[] | null>(null);
    const courseTopicId = useId();
    const budgetId = useId();
    const [generating, setGenerating] = useState(false);

    function toggleTopic(id: string) {
        setSelectedTopicIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
    }

    function toggleTutor(id: string) {
        setSelectedTutorIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
    }

    function handleGenerate() {
        setGenerating(true);
        setPlans(null);

        const budgetNum = Number.parseFloat(budget) || 80;
        const eligibleTutors = MOCK_TUTORS.filter((t) => selectedTutorIds.includes(t.id));
        const topics = MOCK_TOPICS.filter((t) => selectedTopicIds.includes(t.id));

        // Simulate async latency
        setTimeout(() => {
            setPlans([
                buildPlan(topics, eligibleTutors, 'cheapest', budgetNum, selectedTopicIds, preferredLanguage),
                buildPlan(topics, eligibleTutors, 'within_budget', budgetNum, selectedTopicIds, preferredLanguage),
                buildPlan(topics, eligibleTutors, 'best_quality', budgetNum, selectedTopicIds, preferredLanguage),
            ]);
            setGenerating(false);
        }, 900);
    }

    return (
        <div className="mx-auto flex w-full max-w-5xl flex-col gap-10 px-6 py-12">
            {/* Page header */}
            <div className="flex flex-col gap-2">
                <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">AI study plan · POC</p>
                <h1 className="text-2xl font-semibold tracking-tight text-foreground">Generate a study plan</h1>
                <p className="max-w-2xl text-sm leading-relaxed text-muted-foreground">
                    Tell us your course, budget, and the topics you need help with. The AI will suggest three plans —
                    cheapest, within budget, and best quality — using real tutor availability and hourly rates.
                </p>
            </div>

            {/* ----------------------------------------------------------------
                Input section
            ---------------------------------------------------------------- */}
            <section className="grid gap-8 lg:grid-cols-[1fr_1fr]">
                {/* Left — course + budget */}
                <div className="flex flex-col gap-6">
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground" htmlFor={courseTopicId}>
                            Course / module
                        </label>
                        <Input
                            id={courseTopicId}
                            onChange={(e) => setCourseTopic(e.target.value)}
                            placeholder="e.g. Linear Algebra (MA0901)"
                            value={courseTopic}
                        />
                        <p className="text-xs text-muted-foreground">The module name is sent to the AI as context.</p>
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-foreground" htmlFor={budgetId}>
                            Budget (€)
                        </label>
                        <Input
                            id={budgetId}
                            min={0}
                            onChange={(e) => setBudget(e.target.value)}
                            placeholder="80"
                            type="number"
                            value={budget}
                        />
                        <p className="text-xs text-muted-foreground">
                            Used for the "within budget" plan tier. The cheapest and best-quality tiers ignore this.
                        </p>
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <span className="text-sm font-medium text-foreground">Preferred language</span>
                        <div className="flex flex-wrap gap-2">
                            {[
                                { code: 'any', label: 'Any' },
                                { code: 'en', label: 'English' },
                                { code: 'de', label: 'German' },
                                { code: 'es', label: 'Spanish' },
                                { code: 'zh', label: 'Chinese' },
                            ].map((lang) => (
                                <button
                                    className={cn(
                                        'rounded-full border px-3 py-1 text-xs font-medium transition-colors',
                                        preferredLanguage === lang.code
                                            ? 'border-primary bg-primary text-primary-foreground'
                                            : 'border-border bg-card/50 text-muted-foreground hover:border-primary/50 hover:text-foreground',
                                    )}
                                    key={lang.code}
                                    onClick={() => setPreferredLanguage(lang.code)}
                                    type="button"
                                >
                                    {lang.label}
                                </button>
                            ))}
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Only tutors who teach in this language are considered. Falls back to all tutors if none
                            match.
                        </p>
                    </div>

                    {/* Topics */}
                    <div className="flex flex-col gap-3">
                        <div className="flex items-center justify-between">
                            <span className="text-sm font-medium text-foreground">Topics to cover</span>
                            <button
                                className="text-xs text-primary underline-offset-2 hover:underline"
                                onClick={() =>
                                    setSelectedTopicIds(
                                        selectedTopicIds.length === MOCK_TOPICS.length
                                            ? []
                                            : MOCK_TOPICS.map((t) => t.id),
                                    )
                                }
                                type="button"
                            >
                                {selectedTopicIds.length === MOCK_TOPICS.length ? 'Deselect all' : 'Select all'}
                            </button>
                        </div>
                        <div className="flex flex-col gap-2">
                            {MOCK_TOPICS.map((topic) => (
                                <label
                                    className={cn(
                                        'flex cursor-pointer items-start gap-3 rounded-lg border p-3 transition-colors',
                                        selectedTopicIds.includes(topic.id)
                                            ? 'border-primary/40 bg-primary/5'
                                            : 'border-border bg-card/50',
                                    )}
                                    key={topic.id}
                                >
                                    <input
                                        checked={selectedTopicIds.includes(topic.id)}
                                        className="mt-0.5 accent-primary"
                                        onChange={() => toggleTopic(topic.id)}
                                        type="checkbox"
                                    />
                                    <div className="flex flex-col gap-1">
                                        <span className="text-sm font-medium text-foreground">{topic.name}</span>
                                        <div className="flex items-center gap-2">
                                            <DifficultyDots level={topic.difficultyLevel} />
                                            <span className="text-xs text-muted-foreground">
                                                {DIFFICULTY_LABELS[topic.difficultyLevel]}
                                            </span>
                                        </div>
                                    </div>
                                </label>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right — tutor list */}
                <div className="flex flex-col gap-3">
                    <div className="flex items-center justify-between">
                        <span className="text-sm font-medium text-foreground">Available tutors</span>
                        <button
                            className="text-xs text-primary underline-offset-2 hover:underline"
                            onClick={() =>
                                setSelectedTutorIds(
                                    selectedTutorIds.length === MOCK_TUTORS.length ? [] : MOCK_TUTORS.map((t) => t.id),
                                )
                            }
                            type="button"
                        >
                            {selectedTutorIds.length === MOCK_TUTORS.length ? 'Deselect all' : 'Select all'}
                        </button>
                    </div>
                    <p className="text-xs text-muted-foreground">
                        Only selected tutors are considered when generating plans.
                    </p>
                    <div className="flex flex-col gap-2">
                        {MOCK_TUTORS.map((tutor) => (
                            <label
                                className={cn(
                                    'flex cursor-pointer items-start gap-3 rounded-lg border p-3 transition-colors',
                                    selectedTutorIds.includes(tutor.id)
                                        ? 'border-primary/40 bg-primary/5'
                                        : 'border-border bg-card/50',
                                )}
                                key={tutor.id}
                            >
                                <input
                                    checked={selectedTutorIds.includes(tutor.id)}
                                    className="mt-0.5 accent-primary"
                                    onChange={() => toggleTutor(tutor.id)}
                                    type="checkbox"
                                />
                                <div className="flex flex-1 flex-col gap-1">
                                    <div className="flex items-center justify-between gap-2">
                                        <span className="text-sm font-medium text-foreground">{tutor.name}</span>
                                        <span className="text-sm font-semibold text-foreground">
                                            €{tutor.hourlyRate}/h
                                        </span>
                                    </div>
                                    <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-muted-foreground">
                                        <span>
                                            ⭐ {tutor.rating} · {tutor.sessions} sessions
                                        </span>
                                        <span>{tutor.languages.join(', ').toUpperCase()}</span>
                                    </div>
                                    <div className="mt-1 flex flex-wrap gap-1">
                                        {tutor.topicIds.map((tid) => {
                                            const topic = MOCK_TOPICS.find((t) => t.id === tid);
                                            return topic ? (
                                                <span
                                                    className="rounded bg-muted px-1.5 py-0.5 text-xs text-muted-foreground"
                                                    key={tid}
                                                >
                                                    {topic.name.split(' ')[0]}
                                                </span>
                                            ) : null;
                                        })}
                                    </div>
                                </div>
                            </label>
                        ))}
                    </div>
                </div>
            </section>

            {/* Generate button */}
            <div className="flex items-center gap-4">
                <Button
                    disabled={generating || selectedTopicIds.length === 0 || selectedTutorIds.length === 0}
                    onClick={handleGenerate}
                    size="lg"
                >
                    {generating ? 'Generating…' : 'Generate plans'}
                </Button>
                {generating && (
                    <p className="text-sm text-muted-foreground">Asking the AI to build three suggestions…</p>
                )}
            </div>

            {/* ----------------------------------------------------------------
                Results section
            ---------------------------------------------------------------- */}
            {plans && (
                <section className="flex flex-col gap-6">
                    <div className="flex flex-col gap-1">
                        <h2 className="text-xl font-semibold tracking-tight text-foreground">Your plan suggestions</h2>
                        <p className="text-sm text-muted-foreground">
                            Three options for <span className="font-medium text-foreground">{courseTopic}</span> — pick
                            the one that fits your priorities.
                        </p>
                    </div>

                    <div className="grid gap-4 md:grid-cols-3">
                        {plans.map((plan) => (
                            <PlanCard key={plan.tier} plan={plan} />
                        ))}
                    </div>

                    <p className="text-xs text-muted-foreground">
                        * This is a static POC. In production the AI generates the schedule using real tutor
                        availability and milestone deadlines from the API.
                    </p>
                </section>
            )}
        </div>
    );
}
