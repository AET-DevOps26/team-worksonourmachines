import { useId } from 'react';
import { redirect, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import { getMyTutorProfile, updateMyTutorProfile } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
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
import { Textarea } from '~/components/ui/textarea';

export const loader = protectedLoader(async ({ session }) => {
    if (!session.user.roles.includes('tutor')) {
        throw redirect('/tutor/apply');
    }

    const result = await getMyTutorProfile();
    if (isErr(result)) throwRouteError(result.error);
    if (!result.value.profile) {
        throw redirect('/tutor/apply');
    }
    return { profile: result.value.profile };
});

export const action = protectedAction(async ({ request, session }) => {
    if (!session.user.roles.includes('tutor')) {
        throw redirect('/tutor/apply');
    }

    const formData = await request.formData();
    const availability = parseAvailabilityFromFormData(formData);
    const locations = parseLocationsFromFormData(formData);

    const result = await updateMyTutorProfile({
        availability,
        bio: String(formData.get('bio') ?? '').trim(),
        displayName: String(formData.get('displayName') ?? '').trim(),
        hourlyRate: Number(formData.get('hourlyRate') ?? 0),
        languages: String(formData.get('languages') ?? '')
            .split(',')
            .map((l) => l.trim())
            .filter(Boolean),
        locations,
        published: formData.get('published') === 'on',
    });

    if (isErr(result)) {
        return { error: 'Could not update profile.' };
    }
    return { success: true };
});

export default function TutorProfileRoute() {
    const { profile } = useLoaderData<typeof loader>();
    const actionData = useActionData() as { error?: string; success?: boolean } | undefined;
    const navigation = useNavigation();
    const displayNameId = useId();
    const bioId = useId();
    const languagesId = useId();
    const hourlyRateId = useId();

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <CardTitle>Tutor profile</CardTitle>
                <CardDescription>Edit your bio, rates, languages, locations, and availability.</CardDescription>
            </Card>
            <Card>
                <form className="flex flex-col gap-4" method="post">
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={displayNameId}>Display name</Label>
                        <Input defaultValue={profile.displayName} id={displayNameId} name="displayName" required />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={bioId}>Bio</Label>
                        <Textarea defaultValue={profile.bio} id={bioId} name="bio" />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={languagesId}>Languages</Label>
                        <Input defaultValue={profile.languages.join(', ')} id={languagesId} name="languages" />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={hourlyRateId}>Hourly rate (€)</Label>
                        <Input
                            defaultValue={profile.hourlyRate}
                            id={hourlyRateId}
                            min={0}
                            name="hourlyRate"
                            type="number"
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label>Places</Label>
                        <TutorLocationFields locations={profile.locations} />
                    </div>
                    <label className="flex items-center gap-2 text-sm">
                        <input defaultChecked={profile.published} name="published" type="checkbox" />
                        Published (visible in discovery)
                    </label>
                    <div className="flex flex-col gap-2">
                        <Label>Availability</Label>
                        <p className="text-sm text-muted-foreground">
                            Tick the days you are generally available. Add optional details such as time windows.
                        </p>
                        <TutorAvailabilityFields availability={profile.availability} />
                    </div>
                    {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}
                    {actionData?.success ? <p className="text-sm text-emerald-600">Profile saved.</p> : null}
                    <Button disabled={navigation.state === 'submitting'} type="submit">
                        Save profile
                    </Button>
                </form>
            </Card>
        </PageContainer>
    );
}
