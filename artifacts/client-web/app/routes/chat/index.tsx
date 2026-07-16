import { Link, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { listConversations } from '~/.server/service/communication';
import { protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';

export const loader = protectedLoader(async () => {
    const result = await listConversations();
    if (isErr(result)) throw result.error;
    return { conversations: result.value };
});

export default function ChatIndexRoute() {
    const { conversations } = useLoaderData<typeof loader>();

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <CardTitle>Messages</CardTitle>
                <CardDescription>Your conversations with tutors and students.</CardDescription>
            </Card>
            {conversations.length === 0 ? (
                <Card>
                    <CardDescription>No conversations yet. Message a tutor from their profile.</CardDescription>
                </Card>
            ) : (
                conversations.map((conv) => (
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
        </PageContainer>
    );
}
