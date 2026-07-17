import { Link, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import { listConversations } from '~/.server/service/communication';
import { listMyTutorApplications } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { Badge } from '~/components/ui/badge';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

function hasRole(roles: readonly string[], role: string) {
    return roles.includes(role);
}

function statusVariant(status: string) {
    if (status === 'approved') return 'success' as const;
    if (status === 'rejected') return 'danger' as const;
    return 'warning' as const;
}

export const loader = protectedLoader(async ({ session }) => {
    const [conversationsResult, applicationsResult] = await Promise.all([
        listConversations(),
        listMyTutorApplications(),
    ]);
    if (isErr(conversationsResult)) throwRouteError(conversationsResult.error);
    if (isErr(applicationsResult)) throwRouteError(applicationsResult.error);

    return {
        applications: applicationsResult.value,
        conversations: conversationsResult.value,
        isTutor: hasRole(session.user.roles, 'tutor'),
    };
});

export default function DashboardRoute() {
    const { applications, conversations, isTutor } = useLoaderData<typeof loader>();

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <CardTitle>Dashboard</CardTitle>
                <CardDescription>Your messages and activity at a glance.</CardDescription>
                <div className="mt-4 flex flex-wrap gap-2">
                    <Link className={cn(buttonVariants({ size: 'sm' }))} to="/discover">
                        Discover tutors
                    </Link>
                    <Link className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))} to="/me/profile">
                        My profile
                    </Link>
                    {isTutor ? (
                        <Link className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))} to="/tutor/dashboard">
                            Tutor dashboard
                        </Link>
                    ) : (
                        <Link className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))} to="/tutor/apply">
                            Apply as tutor
                        </Link>
                    )}
                </div>
            </Card>

            <section className="flex flex-col gap-3">
                <div className="flex items-center justify-between gap-2">
                    <h2 className="text-lg font-semibold">Recent messages</h2>
                    <Link className="text-sm text-muted-foreground hover:text-foreground" to="/chat">
                        View all
                    </Link>
                </div>
                {conversations.length === 0 ? (
                    <Card>
                        <CardDescription>
                            No conversations yet. Message a tutor from their profile or discover page.
                        </CardDescription>
                    </Card>
                ) : (
                    conversations.slice(0, 5).map((conv) => (
                        <Link key={conv.id} to={`/chat/${conv.id}`}>
                            <Card className="transition-colors hover:border-primary/40">
                                <CardTitle className="text-base">{conv.partner.displayName}</CardTitle>
                                <CardDescription className="mt-1 line-clamp-1">{conv.lastMessage}</CardDescription>
                                <p className="mt-2 text-xs text-muted-foreground" suppressHydrationWarning>
                                    {new Date(conv.updatedAt).toLocaleString()}
                                </p>
                            </Card>
                        </Link>
                    ))
                )}
            </section>

            <section className="flex flex-col gap-3">
                <div className="flex items-center justify-between gap-2">
                    <h2 className="text-lg font-semibold">Tutor applications</h2>
                    <Link
                        className="text-sm text-muted-foreground hover:text-foreground"
                        to={isTutor ? '/tutor/dashboard' : '/tutor/apply'}
                    >
                        {isTutor ? 'View all' : 'Apply'}
                    </Link>
                </div>
                {applications.length === 0 ? (
                    <Card>
                        <CardDescription>
                            No tutor applications yet.{' '}
                            <Link className="underline hover:text-foreground" to="/tutor/apply">
                                Apply for a module
                            </Link>
                        </CardDescription>
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
                            <CardDescription className="mt-2" suppressHydrationWarning>
                                Submitted {new Date(app.submittedAt).toLocaleDateString()}
                            </CardDescription>
                            {app.rejectionReason ? (
                                <p className="mt-2 text-sm text-destructive">Reason: {app.rejectionReason}</p>
                            ) : null}
                        </Card>
                    ))
                )}
            </section>
        </PageContainer>
    );
}
