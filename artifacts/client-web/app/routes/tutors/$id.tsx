import { Form, Link, redirect, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import { startConversation } from '~/.server/service/communication';
import { getTutor } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { formatLocationLabel, TutorAvailabilityDisplay } from '~/components/tutor';
import { Badge } from '~/components/ui/badge';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';

export const loader = protectedLoader(async ({ params, session }) => {
    const result = await getTutor(params.id ?? '');
    if (isErr(result)) {
        throwRouteError(result.error);
    }
    return { isSelf: result.value.userId === session.user.sub, tutor: result.value };
});

export const action = protectedAction(async ({ params, session }) => {
    const tutorResult = await getTutor(params.id ?? '');
    if (isErr(tutorResult)) {
        return { error: 'Could not open this conversation. Please try again.' };
    }
    if (tutorResult.value.userId === session.user.sub) {
        return null;
    }
    const convResult = await startConversation(tutorResult.value.userId);
    if (isErr(convResult)) {
        return { error: 'Could not open this conversation. Please try again.' };
    }
    throw redirect(`/chat/${convResult.value.id}`);
});

export default function PublicTutorProfileRoute() {
    const { tutor, isSelf } = useLoaderData<typeof loader>();
    const actionData = useActionData<typeof action>();
    const navigation = useNavigation();
    const messaging = navigation.state !== 'idle' && navigation.formMethod === 'POST';

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <CardTitle>{tutor.displayName}</CardTitle>
                <CardDescription className="mt-1">
                    €{tutor.hourlyRate}/h · ★ {tutor.ratingSummary.average.toFixed(1)} ({tutor.ratingSummary.count}{' '}
                    reviews)
                </CardDescription>
                <p className="mt-4 text-sm whitespace-pre-wrap text-foreground">{tutor.bio}</p>
                <div className="mt-4 flex flex-wrap gap-2">
                    {tutor.languages.map((l) => (
                        <Badge key={l} variant="outline">
                            {l}
                        </Badge>
                    ))}
                    {tutor.locations.map((l) => (
                        <Badge key={l} variant="outline">
                            {formatLocationLabel(l)}
                        </Badge>
                    ))}
                </div>
                {!isSelf && (
                    <Form className="mt-6" method="post">
                        <Button disabled={messaging} type="submit">
                            {messaging ? 'Opening…' : 'Message tutor'}
                        </Button>
                        {actionData?.error ? <p className="mt-2 text-sm text-destructive">{actionData.error}</p> : null}
                    </Form>
                )}
            </Card>

            <Card>
                <CardTitle className="text-base">Module coverage</CardTitle>
                <div className="mt-3 flex flex-wrap gap-2">
                    {tutor.coverages.map((c) => (
                        <Link key={c.moduleId} to={`/modules/${c.moduleCode}`}>
                            <Badge
                                className="transition-colors hover:border-primary/40 hover:bg-primary/5"
                                variant="outline"
                            >
                                {c.moduleCode} — {c.moduleTitle}
                            </Badge>
                        </Link>
                    ))}
                </div>
            </Card>

            <Card>
                <CardTitle className="text-base">Availability</CardTitle>
                <div className="mt-3">
                    <TutorAvailabilityDisplay availability={tutor.availability} />
                </div>
            </Card>
        </PageContainer>
    );
}
