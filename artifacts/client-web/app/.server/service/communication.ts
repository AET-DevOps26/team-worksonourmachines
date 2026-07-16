import { communicationApi } from '~/.server/api';
import type { SharedCommunicationSendMessageRequest } from '~/.server/api/server-communication/generated';
import { callApi } from '~/.server/service/apiCall';

export async function listConversations() {
    return callApi(() => communicationApi.listConversations());
}

export async function startConversation(participantUserId: string) {
    return callApi(() =>
        communicationApi.startConversation({
            sharedCommunicationStartConversationRequest: { participantUserId },
        }),
    );
}

export async function getConversation(id: string) {
    return callApi(() => communicationApi.getConversation({ id }));
}

export async function listMessages(id: string, page?: number, pageSize?: number) {
    const request: { id: string; page?: number; pageSize?: number } = { id };
    if (page !== undefined) request.page = page;
    if (pageSize !== undefined) request.pageSize = pageSize;
    return callApi(() => communicationApi.listMessages(request));
}

export async function sendMessage(id: string, body: SharedCommunicationSendMessageRequest) {
    return callApi(() => communicationApi.sendMessage({ id, sharedCommunicationSendMessageRequest: body }));
}

export async function createWsTicket() {
    return callApi(() => communicationApi.createWsTicket());
}
