import { Router } from 'express';
import { getReqUser, requireAuth } from '../auth.js';
import { displayNameForUser, newId, state, tutorIdForUser } from '../state.js';

export const communicationRouter = Router();

function parsePagination(pageRaw: unknown, pageSizeRaw: unknown, defaultPageSize: number) {
  const page = Math.max(1, Math.floor(Number(pageRaw)) || 1);
  const pageSize = Math.min(100, Math.max(1, Math.floor(Number(pageSizeRaw)) || defaultPageSize));
  return { page, pageSize };
}

communicationRouter.use(requireAuth);

communicationRouter.get('/conversations', (req, res) => {
  const { sub } = getReqUser(req);
  const summaries = state.conversations
    .filter((c) => c.participantIds.includes(sub))
    .map((c) => {
      const msgs = state.messages
        .filter((m) => m.conversationId === c.id)
        .sort((a, b) => b.sentAt.localeCompare(a.sentAt));
      const last = msgs[0];
      const partnerId = c.participantIds.find((id) => id !== sub)!;
      return {
        id: c.id,
        partner: {
          userId: partnerId,
          displayName: displayNameForUser(partnerId),
          tutorId: tutorIdForUser(partnerId),
        },
        lastMessage: last?.content ?? '',
        lastMessageAt: last?.sentAt ?? c.createdAt,
        updatedAt: c.updatedAt,
      };
    })
    .sort((a, b) => b.updatedAt.localeCompare(a.updatedAt));
  res.json(summaries);
});

communicationRouter.post('/conversations', (req, res) => {
  const { sub } = getReqUser(req);
  const participantUserId = (req.body as { participantUserId?: string }).participantUserId;
  if (!participantUserId) {
    res.status(400).json({ code: 'bad_request', message: 'participantUserId is required' });
    return;
  }
  if (participantUserId === sub) {
    res.status(400).json({ code: 'bad_request', message: 'Cannot chat with yourself' });
    return;
  }
  const existing = state.conversations.find(
    (c) =>
      c.participantIds.includes(sub) &&
      c.participantIds.includes(participantUserId) &&
      c.participantIds.length === 2,
  );
  if (existing) {
    res.json({
      id: existing.id,
      partner: {
        userId: participantUserId,
        displayName: displayNameForUser(participantUserId),
        tutorId: tutorIdForUser(participantUserId),
      },
      createdAt: existing.createdAt,
      updatedAt: existing.updatedAt,
    });
    return;
  }
  const now = new Date().toISOString();
  const conv = {
    id: newId('conv'),
    participantIds: [sub, participantUserId],
    createdAt: now,
    updatedAt: now,
  };
  state.conversations.push(conv);
  res.status(201).json({
    id: conv.id,
    partner: {
      userId: participantUserId,
      displayName: displayNameForUser(participantUserId),
      tutorId: tutorIdForUser(participantUserId),
    },
    createdAt: conv.createdAt,
    updatedAt: conv.updatedAt,
  });
});

communicationRouter.get('/conversations/:id', (req, res) => {
  const { sub } = getReqUser(req);
  const conv = state.conversations.find((c) => c.id === req.params.id);
  if (!conv || !conv.participantIds.includes(sub)) {
    res.status(404).json({ code: 'not_found', message: 'Conversation not found' });
    return;
  }
  const partnerId = conv.participantIds.find((id) => id !== sub)!;
  res.json({
    id: conv.id,
    partner: {
      userId: partnerId,
      displayName: displayNameForUser(partnerId),
      tutorId: tutorIdForUser(partnerId),
    },
    createdAt: conv.createdAt,
    updatedAt: conv.updatedAt,
  });
});

communicationRouter.get('/conversations/:id/messages', (req, res) => {
  const { sub } = getReqUser(req);
  const conv = state.conversations.find((c) => c.id === req.params.id);
  if (!conv || !conv.participantIds.includes(sub)) {
    res.status(404).json({ code: 'not_found', message: 'Conversation not found' });
    return;
  }
  const { page, pageSize } = parsePagination(req.query.page, req.query.pageSize, 50);
  const msgs = state.messages
    .filter((m) => m.conversationId === conv.id)
    .sort((a, b) => a.sentAt.localeCompare(b.sentAt));
  const start = (page - 1) * pageSize;
  res.json({
    items: msgs.slice(start, start + pageSize),
    page,
    pageSize,
    total: msgs.length,
  });
});

communicationRouter.post('/conversations/:id/messages', (req, res) => {
  const { sub } = getReqUser(req);
  const conv = state.conversations.find((c) => c.id === req.params.id);
  if (!conv || !conv.participantIds.includes(sub)) {
    res.status(404).json({ code: 'not_found', message: 'Conversation not found' });
    return;
  }
  const content = (req.body as { content?: string }).content?.trim();
  if (!content) {
    res.status(400).json({ code: 'bad_request', message: 'content is required' });
    return;
  }
  const now = new Date().toISOString();
  const message = {
    id: newId('msg'),
    conversationId: conv.id,
    senderId: sub,
    content,
    sentAt: now,
  };
  state.messages.push(message);
  conv.updatedAt = now;
  res.status(201).json(message);
});
