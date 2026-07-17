import { Link, redirect, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { getStudentProfile, updateStudentProfile } from '~/.server/service/student';
import {
    formatLanguages,
    formatStudyFocusLabel,
    parseLanguages,
    parseStudyFocusFromFormData,
    STUDY_FOCUS_FIELDS,
    StudentProfileForm,
    StudyFocusStarsDisplay,
} from '~/components/profile';
import { PageContainer } from '~/components/shell';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';
export const PROFILE_EDIT_SEARCH = '?edit=1';

export const loader = protectedLoader(async ({ request, session }) => {
    const profileResult = await getStudentProfile();
    if (isErr(profileResult)) {
        throwRouteError(profileResult.error);
    }

    let profile = profileResult.value;
    const nameFromSession = session.user.name?.trim();

    if (!profile.displayName && nameFromSession) {
        const updateResult = await updateStudentProfile({
            bio: profile.bio,
            displayName: nameFromSession,
            languages: profile.languages,
            ...(profile.studyFocus ? { studyFocus: profile.studyFocus } : {}),
        });
        if (isErr(updateResult)) {
            throwRouteError(updateResult.error);
        }
        profile = updateResult.value;
    }

    const url = new URL(request.url);
    const edit = url.searchParams.get('edit') === '1';

    return {
        edit,
        profile,
    };
});

export const action = protectedAction(async ({ request }) => {
    const formData = await request.formData();
    const displayName = String(formData.get('displayName') ?? '').trim();
    const bio = String(formData.get('bio') ?? '').trim();
    const languages = parseLanguages(String(formData.get('languages') ?? ''));
    const studyFocus = parseStudyFocusFromFormData(formData);

    if (!displayName) {
        return { error: 'Display name is required.' };
    }

    if (studyFocus === undefined && [...formData.keys()].some((key) => key.startsWith('studyFocus_'))) {
        return { error: 'Study focus values must be whole numbers from 1 to 5.' };
    }

    const input = {
        bio,
        displayName,
        languages,
        ...(studyFocus ? { studyFocus } : {}),
    };

    const result = await updateStudentProfile(input);
    if (isErr(result)) {
        return { error: 'Could not save profile. Please try again.' };
    }

    return redirect('/me/profile');
});

export default function MyProfileRoute() {
    const { edit, profile } = useLoaderData<typeof loader>();

    if (edit) {
        return (
            <PageContainer className="flex flex-col gap-4">
                <StudentProfileForm
                    bio={profile.bio}
                    description="Manage your display name, bio, languages, and study focus."
                    displayName={profile.displayName}
                    languages={formatLanguages(profile.languages)}
                    showSkipLink={false}
                    {...(profile.studyFocus ? { studyFocus: profile.studyFocus } : {})}
                    submitLabel="Save profile"
                    title="My profile"
                />
                <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/me/profile">
                    Cancel
                </Link>
            </PageContainer>
        );
    }

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <div className="flex items-start justify-between gap-4">
                    <div className="flex flex-col gap-1">
                        <CardTitle>My profile</CardTitle>
                        <CardDescription>Your student profile visible to tutors.</CardDescription>
                    </div>
                    <Link className={cn(buttonVariants({ size: 'sm' }))} to={`/me/profile${PROFILE_EDIT_SEARCH}`}>
                        Edit profile
                    </Link>
                </div>
            </Card>
            <Card className="flex flex-col gap-4">
                <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Display name</p>
                    <p className="mt-1 text-sm">{profile.displayName || '—'}</p>
                </div>
                <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Bio</p>
                    <p className="mt-1 text-sm whitespace-pre-wrap">{profile.bio || '—'}</p>
                </div>
                <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Languages</p>
                    <p className="mt-1 text-sm">
                        {profile.languages.length > 0 ? formatLanguages(profile.languages) : '—'}
                    </p>
                </div>
                {profile.studyFocus ? (
                    <div>
                        <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Study focus</p>
                        <dl className="mt-2 grid gap-3">
                            {STUDY_FOCUS_FIELDS.map((field) => (
                                <div className="flex items-center justify-between gap-4 text-sm" key={field.key}>
                                    <dt className="text-muted-foreground">{formatStudyFocusLabel(field.key)}</dt>
                                    <dd>
                                        <StudyFocusStarsDisplay value={profile.studyFocus?.[field.key] ?? 0} />
                                    </dd>
                                </div>
                            ))}
                        </dl>
                    </div>
                ) : null}
            </Card>
        </PageContainer>
    );
}
