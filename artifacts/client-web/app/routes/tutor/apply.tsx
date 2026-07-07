import { useId } from 'react';
import { redirect, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { getMyTutorProfile, listModules, submitTutorApplication } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import {
    parseAvailabilityFromFormData,
    parseLocationsFromFormData,
    TutorAvailabilityFields,
    TutorLocationFields,
} from '~/components/tutor';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Select } from '~/components/ui/select';
import { Textarea } from '~/components/ui/textarea';

export const loader = protectedLoader(async () => {
    const [modulesResult, tutorResult] = await Promise.all([listModules({ pageSize: 100 }), getMyTutorProfile()]);
    if (isErr(modulesResult)) throw modulesResult.error;
    if (isErr(tutorResult)) throw tutorResult.error;
    return {
        hasProfile: tutorResult.value.profile !== null,
        modules: modulesResult.value.items,
        profile: tutorResult.value.profile,
    };
});

export const action = protectedAction(async ({ request }) => {
    const formData = await request.formData();
    const moduleId = String(formData.get('moduleId') ?? '');
    const certificateRef = String(formData.get('certificateRef') ?? '').trim();
    const isFirstApply = formData.get('isFirstApply') === 'true';

    if (!moduleId || !certificateRef) {
        return { error: 'Module and certificate are required.' };
    }

    const payload: Parameters<typeof submitTutorApplication>[0] = {
        certificateRef,
        moduleId,
    };

    if (isFirstApply) {
        const displayName = String(formData.get('displayName') ?? '').trim();
        if (!displayName) {
            return { error: 'Display name is required for your tutor profile.' };
        }
        const availability = parseAvailabilityFromFormData(formData);
        const locations = parseLocationsFromFormData(formData);

        payload.profile = {
            availability,
            bio: String(formData.get('bio') ?? '').trim(),
            displayName,
            hourlyRate: Number(formData.get('hourlyRate') ?? 0),
            languages: String(formData.get('languages') ?? '')
                .split(',')
                .map((l) => l.trim())
                .filter(Boolean),
            locations,
            published: false,
        };
    }

    const result = await submitTutorApplication(payload);
    if (isErr(result)) {
        return { error: 'Could not submit application. Please try again.' };
    }

    throw redirect('/tutor/dashboard');
});

export default function TutorApplyRoute() {
    const { hasProfile, modules, profile } = useLoaderData<typeof loader>();
    const actionData = useActionData() as { error?: string } | undefined;
    const navigation = useNavigation();
    const isSubmitting = navigation.state === 'submitting';
    const moduleId = useId();
    const certificateRefId = useId();
    const displayNameId = useId();
    const bioId = useId();
    const languagesId = useId();
    const hourlyRateId = useId();

    if (hasProfile) {
        return (
            <div className="mx-auto flex w-full max-w-2xl flex-col gap-6">
                <Card>
                    <CardTitle>Apply for another module</CardTitle>
                    <CardDescription>Submit a certificate for an additional module you want to tutor.</CardDescription>
                </Card>
                <Card>
                    <form className="flex flex-col gap-4" method="post">
                        <input name="isFirstApply" type="hidden" value="false" />
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={moduleId}>Module</Label>
                            <Select id={moduleId} name="moduleId" required>
                                <option value="">Select module</option>
                                {modules.map((m) => (
                                    <option key={m.id} value={m.id}>
                                        {m.code} — {m.title}
                                    </option>
                                ))}
                            </Select>
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={certificateRefId}>Certificate reference</Label>
                            <Input
                                id={certificateRefId}
                                name="certificateRef"
                                placeholder="e.g. cert://my-grade.pdf"
                                required
                            />
                        </div>
                        {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}
                        <Button disabled={isSubmitting} type="submit">
                            {isSubmitting ? 'Submitting…' : 'Submit application'}
                        </Button>
                    </form>
                </Card>
            </div>
        );
    }

    return (
        <div className="mx-auto flex w-full max-w-2xl flex-col gap-6">
            <Card>
                <CardTitle>Apply as tutor</CardTitle>
                <CardDescription>
                    Set up your tutor profile and apply for your first module in one step.
                </CardDescription>
            </Card>
            <Card>
                <form className="flex flex-col gap-6" method="post">
                    <input name="isFirstApply" type="hidden" value="true" />
                    <section className="flex flex-col gap-4">
                        <h2 className="font-semibold">1. Module & certificate</h2>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={moduleId}>Module</Label>
                            <Select defaultValue="" id={moduleId} name="moduleId" required>
                                <option value="">Select module</option>
                                {modules.map((m) => (
                                    <option key={m.id} value={m.id}>
                                        {m.code} — {m.title}
                                    </option>
                                ))}
                            </Select>
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={certificateRefId}>Certificate reference</Label>
                            <Input
                                id={certificateRefId}
                                name="certificateRef"
                                placeholder="e.g. cert://my-grade.pdf"
                                required
                            />
                        </div>
                    </section>

                    <section className="flex flex-col gap-4">
                        <h2 className="font-semibold">2. Tutor profile</h2>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={displayNameId}>Display name</Label>
                            <Input
                                defaultValue={profile?.displayName ?? ''}
                                id={displayNameId}
                                name="displayName"
                                required
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={bioId}>Bio</Label>
                            <Textarea defaultValue={profile?.bio ?? ''} id={bioId} name="bio" />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={languagesId}>Languages (comma-separated)</Label>
                            <Input
                                defaultValue={profile?.languages.join(', ') ?? ''}
                                id={languagesId}
                                name="languages"
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={hourlyRateId}>Hourly rate (€)</Label>
                            <Input
                                defaultValue={profile?.hourlyRate ?? 20}
                                id={hourlyRateId}
                                min={0}
                                name="hourlyRate"
                                required
                                type="number"
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label>Places</Label>
                            <TutorLocationFields {...(profile?.locations ? { locations: profile.locations } : {})} />
                        </div>
                    </section>

                    <section className="flex flex-col gap-4">
                        <h2 className="font-semibold">3. Availability</h2>
                        <p className="text-sm text-muted-foreground">
                            Tick the days you are generally available. Add optional details such as time windows.
                        </p>
                        <TutorAvailabilityFields
                            {...(profile?.availability ? { availability: profile.availability } : {})}
                        />
                    </section>

                    {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}
                    <Button disabled={isSubmitting} type="submit">
                        {isSubmitting ? 'Submitting…' : 'Submit application'}
                    </Button>
                </form>
            </Card>
        </div>
    );
}
