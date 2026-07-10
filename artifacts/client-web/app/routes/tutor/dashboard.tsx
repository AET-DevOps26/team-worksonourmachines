import { Link, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { getMyTutorProfile } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { Badge } from '~/components/ui/badge';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

function statusVariant(status: string) {
    if (status === 'approved') return 'success' as const;
    if (status === 'rejected') return 'danger' as const;
    return 'warning' as const;
}

export const loader = protectedLoader(async () => {
    const result = await getMyTutorProfile();
    if (isErr(result)) throw result.error;
    return result.value;
});

export default function TutorDashboardRoute() {
    const { applications, profile } = useLoaderData<typeof loader>();

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
            <Card>
                <CardTitle>Tutor dashboard</CardTitle>
                <CardDescription>Overview of your applications, profile status, and activity.</CardDescription>
                <div className="mt-4 flex flex-wrap gap-2">
                    <Link className={cn(buttonVariants({ size: 'sm' }))} to="/tutor/profile">
                        Edit profile
                    </Link>
                    <Link className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))} to="/tutor/apply">
                        Apply for module
                    </Link>
                    <Link className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))} to="/chat">
                        Messages
                    </Link>
                </div>
            </Card>

            {profile ? (
                <Card>
                    <CardTitle className="text-base">Profile status</CardTitle>
                    <CardDescription className="mt-2">
                        {profile.published
                            ? 'Your profile is visible in discovery.'
                            : 'Your profile is not published yet.'}
                    </CardDescription>
                    <p className="mt-2 text-sm">Rate: €{profile.hourlyRate}/h</p>
                </Card>
            ) : (
                <Card>
                    <CardDescription>You have not set up a tutor profile yet.</CardDescription>
                    <Link className={cn(buttonVariants({ className: 'mt-4' }))} to="/tutor/apply">
                        Apply as tutor
                    </Link>
                </Card>
            )}

            <section className="flex flex-col gap-3">
                <h2 className="text-lg font-semibold">Module applications</h2>
                {applications.length === 0 ? (
                    <Card>
                        <CardDescription>No applications yet.</CardDescription>
                    </Card>
                ) : (
                    applications.map((app) => (
                        <Card key={app.id}>
                            <div className="flex items-center justify-between gap-2">
                                <CardTitle className="text-base">
                                    {app.moduleCode} — {app.moduleTitle}
                                </CardTitle>
                                <Badge variant={statusVariant(app.status)}>{app.status}</Badge>
                            </div>
                            <CardDescription className="mt-2">
                                Submitted {new Date(app.submittedAt).toLocaleDateString()}
                            </CardDescription>
                            {app.rejectionReason ? (
                                <p className="mt-2 text-sm text-destructive">Reason: {app.rejectionReason}</p>
                            ) : null}
                        </Card>
                    ))
                )}
            </section>
        </div>
    );
}
