import { Link, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { getConversation, listMessages, sendMessage } from '~/.server/service/communication';
import { protectedAction, protectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { cn } from '~/lib/ui/utils';

export const loader = protectedLoader(async ({ params, session }) => {
    const id = params.id ?? '';
    const [convResult, messagesResult] = await Promise.all([getConversation(id), listMessages(id, 1, 100)]);
    if (isErr(convResult)) throw convResult.error;
    if (isErr(messagesResult)) throw messagesResult.error;
    return {
        conversation: convResult.value,
        currentUserId: session.user.sub,
        messages: messagesResult.value.items,
    };
});

export const action = protectedAction(async ({ request, params }) => {
    const formData = await request.formData();
    const content = String(formData.get('content') ?? '').trim();
    if (!content) {
        return { error: 'Message cannot be empty.' };
    }
    const result = await sendMessage(params.id ?? '', { content });
    if (isErr(result)) {
        return { error: 'Failed to send message.' };
    }
    return null;
});

export default function ChatThreadRoute() {
    const { conversation, currentUserId, messages } = useLoaderData<typeof loader>();
    const actionData = useActionData() as { error?: string } | undefined;
    const navigation = useNavigation();
    const partner = conversation.partner;
    const inputKey = messages.at(-1)?.id ?? 'empty';

    return (
        <PageContainer className="flex flex-col gap-4">
            <Card>
                <div className="flex items-center justify-between gap-4">
                    <div>
                        <CardTitle>{partner.displayName}</CardTitle>
                        <CardDescription>Conversation</CardDescription>
                    </div>
                    {partner.tutorId ? (
                        <Link
                            className={cn(buttonVariants({ size: 'sm', variant: 'outline' }))}
                            to={`/tutors/${partner.tutorId}`}
                        >
                            View profile
                        </Link>
                    ) : null}
                </div>
            </Card>

            <Card className="flex min-h-96 flex-col gap-3">
                {messages.map((msg) => (
                    <div
                        className={cn(
                            'max-w-[80%] rounded-lg px-3 py-2 text-sm',
                            msg.senderId === currentUserId
                                ? 'ml-auto bg-primary text-primary-foreground'
                                : 'bg-muted text-foreground',
                        )}
                        key={msg.id}
                    >
                        {msg.content}
                        <p className="mt-1 text-[10px] opacity-70">{new Date(msg.sentAt).toLocaleString()}</p>
                    </div>
                ))}
            </Card>

            <form className="flex flex-col gap-2" method="post">
                <div className="flex gap-2">
                    <Input key={inputKey} name="content" placeholder="Type a message…" required />
                    <Button disabled={navigation.state === 'submitting'} type="submit">
                        Send
                    </Button>
                </div>
                {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}
            </form>
        </PageContainer>
    );
}
