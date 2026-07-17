import { Client } from '@stomp/stompjs';
import { useEffect, useRef, useState } from 'react';
import { Form, Link, useActionData, useLoaderData, useNavigation } from 'react-router';
import type { SharedCommunicationChatMessage } from '~/.server/api/server-communication/generated';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
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
    if (isErr(convResult)) throwRouteError(convResult.error);
    if (isErr(messagesResult)) throwRouteError(messagesResult.error);
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

type ChatMessage = { id: string; senderId: string; content: string; sentAt: string };

function normalise(m: SharedCommunicationChatMessage): ChatMessage {
    return { content: m.content, id: m.id, senderId: m.senderId, sentAt: String(m.sentAt) };
}

export default function ChatThreadRoute() {
    const { conversation, currentUserId, messages: initialMessages } = useLoaderData<typeof loader>();
    const actionData = useActionData() as { error?: string } | undefined;
    const navigation = useNavigation();
    const partner = conversation.partner;

    const [messages, setMessages] = useState<ChatMessage[]>(
        (initialMessages as SharedCommunicationChatMessage[]).map(normalise),
    );
    const [inputValue, setInputValue] = useState('');
    const [liveUpdatesUnavailable, setLiveUpdatesUnavailable] = useState(false);
    const bottomRef = useRef<HTMLDivElement>(null);
    const seenIds = useRef(new Set(initialMessages.map((m) => m.id)));
    const stompClient = useRef<InstanceType<typeof Client> | null>(null);

    useEffect(() => {
        if (navigation.state === 'idle' && !actionData?.error) {
            setInputValue('');
        }
    }, [navigation.state, actionData]);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }, [messages]);

    useEffect(() => {
        let cancelled = false;
        stompClient.current?.deactivate();
        stompClient.current = null;
        setLiveUpdatesUnavailable(false);

        const wsUrl = `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/stomp`;

        void (async () => {
            // Fetch ticket after navigation so opening a chat is not blocked on ticket minting.
            const ticketResponse = await fetch('/chat/ws-ticket', { credentials: 'same-origin' });
            if (!ticketResponse.ok || cancelled) {
                if (!cancelled) {
                    console.error('Failed to obtain WebSocket ticket', ticketResponse.status);
                    setLiveUpdatesUnavailable(true);
                }
                return;
            }
            const ticketBody = (await ticketResponse.json()) as { ticket?: string };
            if (cancelled || !ticketBody.ticket) {
                if (!cancelled) setLiveUpdatesUnavailable(true);
                return;
            }

            const client = new Client({
                brokerURL: wsUrl,
                connectHeaders: { Authorization: `Ticket ${ticketBody.ticket}` },
                onConnect: () => {
                    setLiveUpdatesUnavailable(false);
                    client.subscribe(`/user/queue/conversation.${conversation.id}`, (frame) => {
                        try {
                            const msg = JSON.parse(frame.body) as ChatMessage;
                            if (seenIds.current.has(msg.id)) return;
                            seenIds.current.add(msg.id);
                            setMessages((prev) => [...prev, msg]);
                        } catch {
                            // ignore malformed frames
                        }
                    });
                },
                onStompError: (frame) => {
                    console.error('STOMP error', frame.headers['message'], frame.body);
                    setLiveUpdatesUnavailable(true);
                },
                onWebSocketError: (event) => {
                    console.error('WebSocket error', event);
                    setLiveUpdatesUnavailable(true);
                },
            });

            if (cancelled) {
                return;
            }
            stompClient.current = client;
            client.activate();
        })();

        return () => {
            cancelled = true;
            stompClient.current?.deactivate();
            stompClient.current = null;
        };
    }, [conversation.id]);

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

            {liveUpdatesUnavailable ? (
                <div className="rounded-md border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                    Live updates are temporarily unavailable. You can still send messages; refresh to check for new
                    ones.
                </div>
            ) : null}

            <Card className="flex h-96 flex-col gap-3 overflow-y-auto">
                {messages.map((msg) => (
                    <div
                        className={cn(
                            'max-w-[80%] self-start rounded-lg px-3 py-2 text-sm',
                            msg.senderId === currentUserId
                                ? 'self-end bg-primary text-primary-foreground'
                                : 'bg-muted text-foreground',
                        )}
                        key={msg.id}
                    >
                        {msg.content}
                        <p className="mt-1 text-[10px] opacity-70" suppressHydrationWarning>
                            {new Date(msg.sentAt).toLocaleString()}
                        </p>
                    </div>
                ))}
                <div ref={bottomRef} />
            </Card>

            <Form className="flex flex-col gap-2" method="post">
                <div className="flex gap-2">
                    <Input
                        name="content"
                        onChange={(e) => setInputValue(e.target.value)}
                        placeholder="Type a message…"
                        required
                        value={inputValue}
                    />
                    <Button disabled={navigation.state === 'submitting'} type="submit">
                        Send
                    </Button>
                </div>
                {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}
            </Form>
        </PageContainer>
    );
}
