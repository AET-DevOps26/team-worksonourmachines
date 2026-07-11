import { Link, useLoaderData } from 'react-router';
import type { SharedMarketplaceTutorApplication } from '~/.server/api/server-marketplace/generated';
import { isErr } from '~/.server/lib/result';
import { listConversations } from '~/.server/service/communication';
import { getMyTutorProfile } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { Badge } from '~/components/ui/badge';
import { buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';

function hasRole(roles: readonly string[], role: string) {
    return roles.includes(role);
}

export const loader = protectedLoader(async ({ session }) => {
    const conversationsResult = await listConversations();
    if (isErr(conversationsResult)) throw conversationsResult.error;

    const isTutor = hasRole(session.user.roles, 'tutor');
    let pendingApplications: SharedMarketplaceTutorApplication[] = [];

    if (isTutor) {
        const tutorResult = await getMyTutorProfile();
        if (isErr(tutorResult)) throw tutorResult.error;
        pendingApplications = tutorResult.value.applications.filter((app) => app.status === 'pending');
    }

    return {
        conversations: conversationsResult.value,
        isTutor,
        pendingApplications,
    };
});

export default function DashboardRoute() {
    const { conversations, isTutor, pendingApplications } = useLoaderData<typeof loader>();

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6">
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
                                <p className="mt-2 text-xs text-muted-foreground">
                                    {new Date(conv.updatedAt).toLocaleString()}
                                </p>
                            </Card>
                        </Link>
                    ))
                )}
            </section>

            {isTutor ? (
                <section className="flex flex-col gap-3">
                    <div className="flex items-center justify-between gap-2">
                        <h2 className="text-lg font-semibold">Open applications</h2>
                        <Link className="text-sm text-muted-foreground hover:text-foreground" to="/tutor/dashboard">
                            View all
                        </Link>
                    </div>
                    {pendingApplications.length === 0 ? (
                        <Card>
                            <CardDescription>No pending module applications.</CardDescription>
                        </Card>
                    ) : (
                        pendingApplications.map((app) => (
                            <Card key={app.id}>
                                <div className="flex items-center justify-between gap-2">
                                    <CardTitle className="text-base">
                                        {app.moduleCode} — {app.moduleTitle}
                                    </CardTitle>
                                    <Badge variant="warning">{app.status}</Badge>
                                </div>
                                <CardDescription className="mt-2">
                                    Submitted {new Date(app.submittedAt).toLocaleDateString()}
                                </CardDescription>
                            </Card>
                        ))
                    )}
                </section>
            ) : null}
        </div>
    );
}
