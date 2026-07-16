import { useId } from 'react';
import { Form, Link, redirect, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listModules } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { createGoal } from '~/.server/service/student';
import { PageContainer } from '~/components/shell';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Select } from '~/components/ui/select';
import { Textarea } from '~/components/ui/textarea';
import { cn } from '~/lib/ui/utils';

const LOCATIONS = ['online', 'garching', 'munich', 'weihenstephan', 'straubing', 'ottobrunn'] as const;

export const loader = protectedLoader(async () => {
    const result = await listModules({ pageSize: 100 });
    if (isErr(result)) {
        throw result.error;
    }
    return { modules: result.value.items };
});

export const action = protectedAction(async ({ request }) => {
    const formData = await request.formData();
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
    if (!selfAssessedLevel || selfAssessedLevel < 1 || selfAssessedLevel > 5)
        return { error: 'Self-assessed level must be between 1 and 5.' };

    const result = await createGoal({
        description,
        moduleId,
        selfAssessedLevel,
        targetDate: new Date(targetDate),
        ...(budgetEur != null && !Number.isNaN(budgetEur) ? { budgetEur } : {}),
        locations: locations as (typeof LOCATIONS)[number][],
    });

    if (isErr(result)) {
        return { error: 'Could not create goal. Please try again.' };
    }

    return redirect(`/me/goals/${result.value.id}`);
});

export default function NewLearningGoalRoute() {
    const { modules } = useLoaderData<typeof loader>();
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

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <CardTitle>New learning goal</CardTitle>
                <CardDescription className="mt-1">
                    Define module, target date, budget, and location preferences.
                </CardDescription>
            </Card>

            <Card>
                <Form className="flex flex-col gap-5" method="post">
                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.moduleId}>Module</Label>
                        <Select id={ids.moduleId} name="moduleId" required>
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
                            id={ids.description}
                            name="description"
                            placeholder="What do you want to achieve?"
                            required
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.targetDate}>Target date</Label>
                        <Input
                            id={ids.targetDate}
                            min={new Date().toISOString().slice(0, 10)}
                            name="targetDate"
                            required
                            type="date"
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <Label htmlFor={ids.selfAssessedLevel}>Current level</Label>
                        <Select id={ids.selfAssessedLevel} name="selfAssessedLevel" required>
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
                                    <input name="locations" type="checkbox" value={loc} />
                                    {loc}
                                </label>
                            ))}
                        </div>
                    </div>

                    {actionData?.error && <p className="text-sm text-destructive">{actionData.error}</p>}

                    <div className="flex gap-3">
                        <Button disabled={isSubmitting} type="submit">
                            {isSubmitting ? 'Creating…' : 'Create goal'}
                        </Button>
                        <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/me/goals">
                            Cancel
                        </Link>{' '}
                    </div>
                </Form>
            </Card>
        </PageContainer>
    );
}
