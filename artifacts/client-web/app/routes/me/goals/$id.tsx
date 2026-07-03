import { useId, useState } from 'react';
import { Link, useParams } from 'react-router';
import { Button, buttonVariants } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { cn } from '~/lib/ui/utils';

const LOCATION_LABELS: Record<string, string> = {
    garching: 'Garching',
    munich: 'City Center',
    online: 'Online',
    ottobrun: 'Ottobrunn',
    staubing: 'Staubing',
    weihenstephan: 'Freising',
};

const LOCATIONS = Object.entries(LOCATION_LABELS).map(([code, label]) => ({ code, label }));

const LEVELS = ['beginner', 'intermediate', 'advanced'] as const;

// ---------------------------------------------------------------------------
// Mock data
// ---------------------------------------------------------------------------

type Milestone = { id: string; title: string; dueDate: string; completed: boolean };

type Goal = {
    budgetEur?: number | undefined;
    description: string;
    id: string;
    locations: string[];
    milestones: Milestone[];
    moduleId: string;
    selfAssessedLevel: string;
    targetDate: string;
    topicIds: string[];
};

const MOCK_GOALS: Record<string, Goal> = {
    g1: {
        budgetEur: 80,
        description: 'Prepare for the final exam with focus on matrix decomposition and eigenvalues.',
        id: 'g1',
        locations: ['garching', 'online'],
        milestones: [
            { completed: true, dueDate: '2026-07-10', id: 'm1', title: 'Complete practice problem sets' },
            { completed: false, dueDate: '2026-07-20', id: 'm2', title: 'Review all lecture notes' },
            { completed: false, dueDate: '2026-07-31', id: 'm3', title: 'Final exam' },
        ],
        moduleId: 'MA0901',
        selfAssessedLevel: 'intermediate',
        targetDate: '2026-07-31',
        topicIds: ['t1', 't2', 't3'],
    },
    g2: {
        description: 'Get comfortable with graph algorithms before the midterm.',
        id: 'g2',
        locations: ['online'],
        milestones: [{ completed: false, dueDate: '2026-08-15', id: 'm4', title: 'Midterm exam' }],
        moduleId: 'IN0011',
        selfAssessedLevel: 'beginner',
        targetDate: '2026-08-15',
        topicIds: ['t4', 't5'],
    },
};

// ---------------------------------------------------------------------------
// Milestone row
// ---------------------------------------------------------------------------

function MilestoneRow({ milestone, onToggle }: { milestone: Milestone; onToggle: (id: string) => void }) {
    return (
        <div className="flex items-center gap-3 rounded-lg border border-border bg-card px-4 py-3">
            <button
                aria-label={milestone.completed ? 'Mark incomplete' : 'Mark complete'}
                className={cn(
                    'flex size-5 shrink-0 items-center justify-center rounded-full border-2 transition-colors',
                    milestone.completed
                        ? 'border-primary bg-primary text-primary-foreground'
                        : 'border-muted-foreground/40 hover:border-primary',
                )}
                onClick={() => onToggle(milestone.id)}
                type="button"
            >
                {milestone.completed && (
                    <svg className="size-3" fill="none" stroke="currentColor" strokeWidth={3} viewBox="0 0 12 12">
                        <path d="M2 6l3 3 5-5" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                )}
            </button>
            <div className="flex flex-1 flex-col gap-0.5">
                <span
                    className={cn('text-sm font-medium', milestone.completed && 'text-muted-foreground line-through')}
                >
                    {milestone.title}
                </span>
                <span className="text-xs text-muted-foreground">
                    Due{' '}
                    {new Date(milestone.dueDate).toLocaleDateString('en-GB', {
                        day: 'numeric',
                        month: 'short',
                        year: 'numeric',
                    })}
                </span>
            </div>
        </div>
    );
}

// ---------------------------------------------------------------------------
// Add milestone form
// ---------------------------------------------------------------------------

function AddMilestoneForm({ onAdd }: { onAdd: (title: string, dueDate: string) => void }) {
    const titleId = useId();
    const dateId = useId();
    const [title, setTitle] = useState('');
    const [dueDate, setDueDate] = useState('');
    const [open, setOpen] = useState(false);

    function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        if (!title.trim() || !dueDate) return;
        onAdd(title.trim(), dueDate);
        setTitle('');
        setDueDate('');
        setOpen(false);
    }

    if (!open) {
        return (
            <button
                className="flex items-center gap-2 text-xs font-medium text-primary underline-offset-2 hover:underline"
                onClick={() => setOpen(true)}
                type="button"
            >
                + Add milestone
            </button>
        );
    }

    return (
        <form className="flex flex-col gap-3 rounded-lg border border-dashed border-border p-4" onSubmit={handleSubmit}>
            <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium text-foreground" htmlFor={titleId}>
                    Title
                </label>
                <Input
                    autoFocus
                    id={titleId}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="e.g. Finish practice exams"
                    value={title}
                />
            </div>
            <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium text-foreground" htmlFor={dateId}>
                    Due date
                </label>
                <Input id={dateId} onChange={(e) => setDueDate(e.target.value)} type="date" value={dueDate} />
            </div>
            <div className="flex gap-2">
                <Button disabled={!title.trim() || !dueDate} size="sm" type="submit">
                    Add
                </Button>
                <Button onClick={() => setOpen(false)} size="sm" type="button" variant="ghost">
                    Cancel
                </Button>
            </div>
        </form>
    );
}

// ---------------------------------------------------------------------------
// Main route
// ---------------------------------------------------------------------------

export default function LearningGoalDetailRoute() {
    const { id } = useParams();
    const goalData = id ? MOCK_GOALS[id] : undefined;

    const descriptionId = useId();
    const targetDateId = useId();
    const budgetId = useId();

    const [goal, setGoal] = useState<Goal | undefined>(goalData);

    if (!goal) {
        return (
            <div className="mx-auto flex w-full max-w-xl flex-col gap-6 px-6 py-12">
                <Link className="text-xs text-muted-foreground underline-offset-2 hover:underline" to="/me/goals">
                    ← My goals
                </Link>
                <p className="text-sm text-muted-foreground">Goal not found.</p>
            </div>
        );
    }

    function toggleMilestone(milestoneId: string) {
        setGoal((prev) => {
            if (!prev) return prev;
            return {
                ...prev,
                milestones: prev.milestones.map((m) => (m.id === milestoneId ? { ...m, completed: !m.completed } : m)),
            };
        });
    }

    function addMilestone(title: string, dueDate: string) {
        setGoal((prev) => {
            if (!prev) return prev;
            const newMilestone: Milestone = {
                completed: false,
                dueDate,
                id: `m-${Date.now()}`,
                title,
            };
            return { ...prev, milestones: [...prev.milestones, newMilestone] };
        });
    }

    const completedCount = goal.milestones.filter((m) => m.completed).length;

    return (
        <div className="mx-auto flex w-full max-w-xl flex-col gap-8 px-6 py-12">
            <div className="flex flex-col gap-1">
                <Link className="text-xs text-muted-foreground underline-offset-2 hover:underline" to="/me/goals">
                    ← My goals
                </Link>
                <div className="mt-2 flex items-start justify-between gap-3">
                    <div className="flex flex-col gap-0.5">
                        <span className="text-xs font-medium text-muted-foreground">{goal.moduleId}</span>
                        <h1 className="text-2xl font-semibold tracking-tight text-foreground">Learning goal</h1>
                    </div>
                    <Link className={buttonVariants({ size: 'sm', variant: 'outline' })} to="/plans/new">
                        Generate study plan
                    </Link>
                </div>
            </div>

            {/* Edit form */}
            <form
                className="flex flex-col gap-5"
                onSubmit={(e) => {
                    e.preventDefault();
                    // TODO: PUT /v1/students/me/goals/:id
                }}
            >
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-foreground" htmlFor={descriptionId}>
                        Description
                    </label>
                    <textarea
                        className="min-h-[72px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                        id={descriptionId}
                        onChange={(e) => setGoal((g) => g && { ...g, description: e.target.value })}
                        value={goal.description}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-foreground" htmlFor={targetDateId}>
                        Target date
                    </label>
                    <Input
                        id={targetDateId}
                        onChange={(e) => setGoal((g) => g && { ...g, targetDate: e.target.value })}
                        type="date"
                        value={goal.targetDate}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <span className="text-sm font-medium text-foreground">Self-assessed level</span>
                    <div className="flex gap-2">
                        {LEVELS.map((level) => (
                            <button
                                className={cn(
                                    'rounded-full border px-3 py-1 text-xs font-medium capitalize transition-colors',
                                    goal.selfAssessedLevel === level
                                        ? 'border-primary bg-primary text-primary-foreground'
                                        : 'border-border bg-card/50 text-muted-foreground hover:border-primary/50 hover:text-foreground',
                                )}
                                key={level}
                                onClick={() => setGoal((g) => g && { ...g, selfAssessedLevel: level })}
                                type="button"
                            >
                                {level}
                            </button>
                        ))}
                    </div>
                </div>

                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-foreground" htmlFor={budgetId}>
                        Budget (€) <span className="font-normal text-muted-foreground">— optional</span>
                    </label>
                    <Input
                        id={budgetId}
                        min={0}
                        onChange={(e) =>
                            setGoal(
                                (g) => g && { ...g, budgetEur: e.target.value ? Number(e.target.value) : undefined },
                            )
                        }
                        placeholder="e.g. 80"
                        type="number"
                        value={goal.budgetEur ?? ''}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <span className="text-sm font-medium text-foreground">Preferred locations</span>
                    <div className="flex flex-wrap gap-2">
                        {LOCATIONS.map((loc) => (
                            <button
                                className={cn(
                                    'rounded-full border px-3 py-1 text-xs font-medium transition-colors',
                                    goal.locations.includes(loc.code)
                                        ? 'border-primary bg-primary text-primary-foreground'
                                        : 'border-border bg-card/50 text-muted-foreground hover:border-primary/50 hover:text-foreground',
                                )}
                                key={loc.code}
                                onClick={() =>
                                    setGoal((g) => {
                                        if (!g) return g;
                                        const locs = g.locations.includes(loc.code)
                                            ? g.locations.filter((l) => l !== loc.code)
                                            : [...g.locations, loc.code];
                                        return { ...g, locations: locs };
                                    })
                                }
                                type="button"
                            >
                                {loc.label}
                            </button>
                        ))}
                    </div>
                </div>

                <Button className="self-start" type="submit">
                    Save changes
                </Button>
            </form>

            {/* Milestones */}
            <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between">
                    <h2 className="text-base font-semibold text-foreground">Milestones</h2>
                    {goal.milestones.length > 0 && (
                        <span className="text-xs text-muted-foreground">
                            {completedCount} / {goal.milestones.length} complete
                        </span>
                    )}
                </div>

                {goal.milestones.length > 0 ? (
                    <div className="flex flex-col gap-2">
                        {goal.milestones.map((m) => (
                            <MilestoneRow key={m.id} milestone={m} onToggle={toggleMilestone} />
                        ))}
                    </div>
                ) : (
                    <p className="text-sm text-muted-foreground">No milestones yet.</p>
                )}

                <AddMilestoneForm onAdd={addMilestone} />
            </div>
        </div>
    );
}
