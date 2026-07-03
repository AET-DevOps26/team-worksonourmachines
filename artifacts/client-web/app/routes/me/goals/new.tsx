import { useId, useState } from 'react';
import { Link } from 'react-router';
import { Button, buttonVariants } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { cn } from '~/lib/ui/utils';

const LOCATIONS = [
    { code: 'garching', label: 'Garching' },
    { code: 'munich', label: 'City Center' },
    { code: 'weihenstephan', label: 'Freising' },
    { code: 'staubing', label: 'Staubing' },
    { code: 'ottobrun', label: 'Ottobrunn' },
    { code: 'online', label: 'Online' },
];

const LEVELS = ['beginner', 'intermediate', 'advanced'] as const;

export default function NewLearningGoalRoute() {
    const moduleIdId = useId();
    const descriptionId = useId();
    const targetDateId = useId();
    const budgetId = useId();

    const [moduleId, setModuleId] = useState('');
    const [description, setDescription] = useState('');
    const [targetDate, setTargetDate] = useState('');
    const [selfAssessedLevel, setSelfAssessedLevel] = useState<(typeof LEVELS)[number]>('beginner');
    const [budgetEur, setBudgetEur] = useState('');
    const [locations, setLocations] = useState<string[]>([]);

    function toggleLocation(code: string) {
        setLocations((prev) => (prev.includes(code) ? prev.filter((l) => l !== code) : [...prev, code]));
    }

    const canSubmit = moduleId.trim() && description.trim() && targetDate && locations.length > 0;

    return (
        <div className="mx-auto flex w-full max-w-xl flex-col gap-8 px-6 py-12">
            <div className="flex flex-col gap-1">
                <Link className="text-xs text-muted-foreground underline-offset-2 hover:underline" to="/me/goals">
                    ← My goals
                </Link>
                <h1 className="mt-2 text-2xl font-semibold tracking-tight text-foreground">New learning goal</h1>
                <p className="text-sm text-muted-foreground">
                    Define what you want to achieve, when, and with how much budget.
                </p>
            </div>

            <form
                className="flex flex-col gap-6"
                onSubmit={(e) => {
                    e.preventDefault();
                    // TODO: POST /v1/students/me/goals
                }}
            >
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-foreground" htmlFor={moduleIdId}>
                        Module
                    </label>
                    <Input
                        id={moduleIdId}
                        onChange={(e) => setModuleId(e.target.value)}
                        placeholder="e.g. MA0901"
                        value={moduleId}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-foreground" htmlFor={descriptionId}>
                        Description
                    </label>
                    <textarea
                        className="min-h-[80px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                        id={descriptionId}
                        onChange={(e) => setDescription(e.target.value)}
                        placeholder="What do you want to achieve?"
                        value={description}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-foreground" htmlFor={targetDateId}>
                        Target date
                    </label>
                    <Input
                        id={targetDateId}
                        onChange={(e) => setTargetDate(e.target.value)}
                        type="date"
                        value={targetDate}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <span className="text-sm font-medium text-foreground">Self-assessed level</span>
                    <div className="flex gap-2">
                        {LEVELS.map((level) => (
                            <button
                                className={cn(
                                    'rounded-full border px-3 py-1 text-xs font-medium capitalize transition-colors',
                                    selfAssessedLevel === level
                                        ? 'border-primary bg-primary text-primary-foreground'
                                        : 'border-border bg-card/50 text-muted-foreground hover:border-primary/50 hover:text-foreground',
                                )}
                                key={level}
                                onClick={() => setSelfAssessedLevel(level)}
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
                        onChange={(e) => setBudgetEur(e.target.value)}
                        placeholder="e.g. 80"
                        type="number"
                        value={budgetEur}
                    />
                </div>

                <div className="flex flex-col gap-1.5">
                    <span className="text-sm font-medium text-foreground">Preferred locations</span>
                    <div className="flex flex-wrap gap-2">
                        {LOCATIONS.map((loc) => (
                            <button
                                className={cn(
                                    'rounded-full border px-3 py-1 text-xs font-medium transition-colors',
                                    locations.includes(loc.code)
                                        ? 'border-primary bg-primary text-primary-foreground'
                                        : 'border-border bg-card/50 text-muted-foreground hover:border-primary/50 hover:text-foreground',
                                )}
                                key={loc.code}
                                onClick={() => toggleLocation(loc.code)}
                                type="button"
                            >
                                {loc.label}
                            </button>
                        ))}
                    </div>
                    <p className="text-xs text-muted-foreground">Select at least one location.</p>
                </div>

                <div className="flex items-center gap-3">
                    <Button disabled={!canSubmit} type="submit">
                        Create goal
                    </Button>
                    <Link className={buttonVariants({ variant: 'ghost' })} to="/me/goals">
                        Cancel
                    </Link>
                </div>
            </form>
        </div>
    );
}
