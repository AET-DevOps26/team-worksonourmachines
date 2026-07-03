import { Link } from 'react-router';
import { buttonVariants } from '~/components/ui/button';
import { cn } from '~/lib/ui/utils';

const LOCATION_LABELS: Record<string, string> = {
    garching: 'Garching',
    munich: 'City Center',
    online: 'Online',
    ottobrun: 'Ottobrunn',
    staubing: 'Staubing',
    weihenstephan: 'Freising',
};

const LEVEL_STYLES: Record<string, string> = {
    advanced: 'bg-amber-500/15 text-amber-700 dark:text-amber-300',
    beginner: 'bg-emerald-500/15 text-emerald-700 dark:text-emerald-300',
    intermediate: 'bg-blue-500/15 text-blue-700 dark:text-blue-300',
};

const MOCK_GOALS = [
    {
        budgetEur: 80,
        description: 'Prepare for the final exam with focus on matrix decomposition and eigenvalues.',
        id: 'g1',
        locations: ['garching', 'online'],
        moduleId: 'MA0901',
        moduleName: 'Linear Algebra',
        selfAssessedLevel: 'intermediate',
        targetDate: '2026-07-31',
        topicIds: ['t1', 't2', 't3'],
    },
    {
        budgetEur: undefined,
        description: 'Get comfortable with graph algorithms before the midterm.',
        id: 'g2',
        locations: ['online'],
        moduleId: 'IN0011',
        moduleName: 'Algorithms & Data Structures',
        selfAssessedLevel: 'beginner',
        targetDate: '2026-08-15',
        topicIds: ['t4', 't5'],
    },
];

export default function LearningGoalsRoute() {
    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-8 px-6 py-12">
            <div className="flex items-start justify-between gap-4">
                <div className="flex flex-col gap-1">
                    <h1 className="text-2xl font-semibold tracking-tight text-foreground">My goals</h1>
                    <p className="text-sm text-muted-foreground">
                        Your learning goals across the modules you are studying.
                    </p>
                </div>
                <Link className={buttonVariants({ size: 'sm' })} to="/me/goals/new">
                    New goal
                </Link>
            </div>

            {MOCK_GOALS.length === 0 ? (
                <div className="rounded-xl border border-dashed border-border py-16 text-center text-sm text-muted-foreground">
                    No goals yet.{' '}
                    <Link className="text-primary underline-offset-2 hover:underline" to="/me/goals/new">
                        Create your first goal
                    </Link>
                    .
                </div>
            ) : (
                <div className="flex flex-col gap-4">
                    {MOCK_GOALS.map((goal) => (
                        <Link
                            className="flex flex-col gap-3 rounded-xl border border-border bg-card p-5 shadow-sm transition-colors hover:border-primary/40"
                            key={goal.id}
                            to={`/me/goals/${goal.id}`}
                        >
                            <div className="flex items-start justify-between gap-3">
                                <div className="flex flex-col gap-1">
                                    <span className="text-xs font-medium text-muted-foreground">{goal.moduleId}</span>
                                    <h2 className="text-base font-semibold text-foreground">{goal.moduleName}</h2>
                                    <p className="text-sm text-muted-foreground">{goal.description}</p>
                                </div>
                                <span
                                    className={cn(
                                        'shrink-0 rounded-full px-2.5 py-0.5 text-xs font-semibold capitalize',
                                        LEVEL_STYLES[goal.selfAssessedLevel] ?? 'bg-muted text-muted-foreground',
                                    )}
                                >
                                    {goal.selfAssessedLevel}
                                </span>
                            </div>

                            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
                                <span>
                                    Due{' '}
                                    {new Date(goal.targetDate).toLocaleDateString('en-GB', {
                                        day: 'numeric',
                                        month: 'short',
                                        year: 'numeric',
                                    })}
                                </span>
                                {goal.budgetEur !== undefined && <span>Budget €{goal.budgetEur}</span>}
                                <span>
                                    {goal.topicIds.length} topic{goal.topicIds.length !== 1 ? 's' : ''}
                                </span>
                                <div className="flex flex-wrap gap-1">
                                    {goal.locations.map((loc) => (
                                        <span className="rounded bg-muted px-1.5 py-0.5 capitalize" key={loc}>
                                            {LOCATION_LABELS[loc] ?? loc}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
}
