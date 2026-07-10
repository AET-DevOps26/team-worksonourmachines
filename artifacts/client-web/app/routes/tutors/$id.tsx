import { Form, Link, redirect, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { startConversation } from '~/.server/service/communication';
import { getTutor } from '~/.server/service/marketplace';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { formatLocationLabel, TutorAvailabilityDisplay } from '~/components/tutor';
import { Badge } from '~/components/ui/badge';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';

export const loader = protectedLoader(async ({ params }) => {
    const result = await getTutor(params.id ?? '');
    if (isErr(result)) {
        throw result.error;
    }
    return { tutor: result.value };
});

export const action = protectedAction(async ({ params }) => {
    const tutorResult = await getTutor(params.id ?? '');
    if (isErr(tutorResult)) {
        throw tutorResult.error;
    }
    const convResult = await startConversation(tutorResult.value.userId);
    if (isErr(convResult)) {
        throw convResult.error;
    }
    throw redirect(`/chat/${convResult.value.id}`);
});

export default function PublicTutorProfileRoute() {
    const { tutor } = useLoaderData<typeof loader>();

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
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
                <Form className="mt-6" method="post">
                    <Button type="submit">Message tutor</Button>
                </Form>
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
        </div>
    );
}
