import { useId } from 'react';
import { Form, Link, redirect, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listModules } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { deleteGoal, getGoal, updateGoal } from '~/.server/service/student';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Select } from '~/components/ui/select';
import { Textarea } from '~/components/ui/textarea';
import { cn } from '~/lib/ui/utils';

const LOCATIONS = ['online', 'garching', 'munich', 'weihenstephan', 'straubing', 'ottobrunn'] as const;

export const loader = protectedLoader(async ({ params }) => {
    const id = params.id ?? '';
    const [goalResult, modulesResult] = await Promise.all([getGoal(id), listModules({ pageSize: 100 })]);
    if (isErr(goalResult)) throw goalResult.error;
    if (isErr(modulesResult)) throw modulesResult.error;
    return { goal: goalResult.value, modules: modulesResult.value.items };
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

    if (_action === 'update') {
        const moduleId = String(formData.get('moduleId') ?? '').trim();
        const description = String(formData.get('description') ?? '').trim();
        const targetDate = String(formData.get('targetDate') ?? '').trim();
        const selfAssessedLevel = Number(formData.get('selfAssessedLevel') ?? 0);
        const budgetRaw = formData.get('budgetEur');
        const budgetEur = budgetRaw ? Number(budgetRaw) : undefined;
        const locations = formData.getAll('locations').map(String);

        if (!moduleId) return { error: 'Module is required.' };
        if (!description) return { error: 'Description is required.' };
        if (!targetDate) return { error: 'Target date is required.' };
        if (!selfAssessedLevel || selfAssessedLevel < 1 || selfAssessedLevel > 5) {
            return { error: 'Self-assessed level must be between 1 and 5.' };
        }

        const result = await updateGoal(id, {
            description,
            moduleId,
            selfAssessedLevel,
            targetDate: new Date(targetDate),
            ...(budgetEur != null && !Number.isNaN(budgetEur) ? { budgetEur } : {}),
            locations: locations as (typeof LOCATIONS)[number][],
        });

        if (isErr(result)) {
            return { error: 'Could not update goal. Please try again.' };
        }

        return redirect(`/me/goals/${result.value.id}`);
    }

    return { error: 'Unknown action.' };
});

export default function LearningGoalDetailRoute() {
    const { goal, modules } = useLoaderData<typeof loader>();
    const actionData = useActionData<typeof action>();
    const navigation = useNavigation();
    const isSubmitting = navigation.state !== 'idle';
    const id = useId();
    const ids = {
        budgetEur: `${id}-budgetEur`,
        description: `${id}-description`,
        moduleId: `${id}-moduleId`,
        selfAssessedLevel: `${id}-selfAssessedLevel`,
        targetDate: `${id}-targetDate`,
    };
    const targetDateValue = new Date(goal.targetDate).toISOString().slice(0, 10);
    const selectedLocations = new Set(goal.locations);

    return (
        <div className="mx-auto flex w-full max-w-2xl flex-col gap-6">
            <Card>
                <div className="flex items-start justify-between gap-4">
                    <div>
                        <CardTitle>Edit learning goal</CardTitle>
                        <CardDescription className="mt-1">
                            Update module, target date, budget, and locations.
                        </CardDescription>
                    </div>
                    <Link className={cn(buttonVariants({ size: 'sm' }))} to={`/plans/${goal.id}`}>
                        Study plan
                    </Link>
                </div>
            </Card>

            <Card>
                <Form className="flex flex-col gap-5" method="post">
                    <input name="_action" type="hidden" value="update" />

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.moduleId}>Module</Label>
                        <Select defaultValue={goal.moduleId} id={ids.moduleId} name="moduleId" required>
                            <option value="">Select a module…</option>
                            {modules.map((m) => (
                                <option key={m.id} value={m.id}>
                                    {m.code} — {m.title}
                                </option>
                            ))}
                        </Select>
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.description}>Description</Label>
                        <Textarea
                            defaultValue={goal.description}
                            id={ids.description}
                            name="description"
                            placeholder="What do you want to achieve?"
                            required
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.targetDate}>Target date</Label>
                        <Input
                            defaultValue={targetDateValue}
                            id={ids.targetDate}
                            name="targetDate"
                            required
                            type="date"
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.selfAssessedLevel}>Current level</Label>
                        <Select
                            defaultValue={String(goal.selfAssessedLevel)}
                            id={ids.selfAssessedLevel}
                            name="selfAssessedLevel"
                            required
                        >
                            <option value="">Select level…</option>
                            <option value="1">1 — Beginner</option>
                            <option value="2">2 — Elementary</option>
                            <option value="3">3 — Intermediate</option>
                            <option value="4">4 — Advanced</option>
                            <option value="5">5 — Expert</option>
                        </Select>
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.budgetEur}>Budget (€, optional)</Label>
                        <Input
                            defaultValue={goal.budgetEur ?? ''}
                            id={ids.budgetEur}
                            min="0"
                            name="budgetEur"
                            placeholder="e.g. 200"
                            step="1"
                            type="number"
                        />
                    </div>

                    <div className="flex flex-col gap-2">
                        <Label>Preferred locations</Label>
                        <div className="flex flex-wrap gap-3">
                            {LOCATIONS.map((loc) => (
                                <label className="flex items-center gap-1.5 text-sm capitalize" key={loc}>
                                    <input
                                        defaultChecked={selectedLocations.has(loc)}
                                        name="locations"
                                        type="checkbox"
                                        value={loc}
                                    />
                                    {loc}
                                </label>
                            ))}
                        </div>
                    </div>

                    {actionData?.error && <p className="text-sm text-destructive">{actionData.error}</p>}

                    <div className="flex flex-wrap gap-3">
                        <Button disabled={isSubmitting} type="submit">
                            {isSubmitting ? 'Saving…' : 'Save changes'}
                        </Button>
                        <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/me/goals">
                            ← Back to goals
                        </Link>
                    </div>
                </Form>
            </Card>

            <Form method="post">
                <input name="_action" type="hidden" value="delete" />
                <Button size="sm" type="submit" variant="destructive">
                    Delete goal
                </Button>
            </Form>
        </div>
    );
}
