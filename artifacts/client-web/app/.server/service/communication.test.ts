import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ErrorResponse } from '~/.server/api/error';

const listConversations = vi.fn();
const startConversation = vi.fn();
const sendMessage = vi.fn();

vi.mock('~/.server/api', () => ({
    communicationApi: {
        listConversations,
        sendMessage,
        startConversation,
    },
}));

describe('communication service', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('returns Ok for a successful listConversations call', async () => {
        const conversations = [{ id: 'c1' }];
        listConversations.mockResolvedValue(conversations);

        const { listConversations: listConversationsService } = await import('./communication');
        const result = await listConversationsService();

        expect(listConversations).toHaveBeenCalledOnce();
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toEqual(conversations);
        }
    });

    it('maps API ErrorResponse to Err', async () => {
        startConversation.mockRejectedValue(new ErrorResponse('forbidden'));

        const { startConversation: startConversationService } = await import('./communication');
        const result = await startConversationService('other-user');

        expect(startConversation).toHaveBeenCalledWith({
            sharedCommunicationStartConversationRequest: { participantUserId: 'other-user' },
        });
        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('forbidden');
        }
    });

    it('maps unexpected throws to Err(unknown)', async () => {
        sendMessage.mockRejectedValue(new TypeError('failed to fetch'));

        const { sendMessage: sendMessageService } = await import('./communication');
        const result = await sendMessageService('c1', { body: 'hello' } as never);

        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error.type).toBe('unknown');
        }
    });
});
