import { beforeEach, describe, expect, it, vi } from 'vitest';
import { sampleSession } from '~/.server/test/fixtures';

const store = new Map<string, string>();

const mockRedis = {
    connect: vi.fn(async () => undefined),
    del: vi.fn(async (key: string) => {
        store.delete(key);
        return 1;
    }),
    get: vi.fn(async (key: string) => store.get(key) ?? null),
    on: vi.fn(),
    set: vi.fn(async (key: string, value: string) => {
        store.set(key, value);
        return 'OK';
    }),
};

vi.mock('redis', () => ({
    createClient: vi.fn(() => mockRedis),
}));

vi.mock('~/.server/lib/logger', () => ({
    logger: {
        debug: vi.fn(),
        error: vi.fn(),
        info: vi.fn(),
        warn: vi.fn(),
    },
}));

describe('session service', () => {
    beforeEach(() => {
        store.clear();
        vi.clearAllMocks();
    });

    it('creates and loads a session by id', async () => {
        const session = sampleSession({ refreshToken: 'refresh' });
        const {
            createSession,
            getSessionById,
            createSidCookie,
            getSidFromRequest,
            clearSidCookie,
            createSidTxnCookie,
            getSidTxnFromRequest,
        } = await import('./session');

        const createResult = await createSession(session);
        expect(createResult.isOk).toBe(true);
        if (createResult.isErr) {
            return;
        }

        const sessionId = createResult.value;
        const loaded = await getSessionById(sessionId);
        expect(loaded.isOk).toBe(true);
        if (loaded.isOk) {
            expect(loaded.value).toEqual(session);
        }

        const cookie = createSidCookie(sessionId);
        const request = new Request('http://localhost/', { headers: { cookie } });
        expect(getSidFromRequest(request)).toBe(sessionId);

        const txnCookie = createSidTxnCookie('txn-1');
        const txnRequest = new Request('http://localhost/', { headers: { cookie: txnCookie } });
        expect(getSidTxnFromRequest(txnRequest)).toBe('txn-1');
        expect(clearSidCookie()).toContain('sid=');
    });

    it('returns null for a missing session id', async () => {
        const { getSessionById } = await import('./session');
        const result = await getSessionById('missing');

        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toBeNull();
        }
    });

    it('returns null for invalid session JSON', async () => {
        store.set('session:bad', '{not-json');
        const { getSessionById } = await import('./session');

        const result = await getSessionById('bad');
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toBeNull();
        }
    });

    it('returns null for structurally invalid session payloads', async () => {
        store.set('session:invalid', JSON.stringify({ accessToken: 1 }));
        const { getSessionById } = await import('./session');

        const result = await getSessionById('invalid');
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toBeNull();
        }
    });

    it('loads a session entry from the sid cookie', async () => {
        const session = sampleSession();
        const { createSession, createSidCookie, getSessionEntryFromRequest } = await import('./session');

        const createResult = await createSession(session);
        expect(createResult.isOk).toBe(true);
        if (createResult.isErr) {
            return;
        }

        const request = new Request('http://localhost/', {
            headers: { cookie: createSidCookie(createResult.value) },
        });
        const entry = await getSessionEntryFromRequest(request);

        expect(entry.isOk).toBe(true);
        if (entry.isOk) {
            expect(entry.value).toEqual({ id: createResult.value, session });
        }
    });

    it('returns null when the request has no sid cookie', async () => {
        const { getSessionEntryFromRequest } = await import('./session');
        const result = await getSessionEntryFromRequest(new Request('http://localhost/'));

        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toBeNull();
        }
    });

    it('deletes a dangling sid and returns null', async () => {
        const { createSidCookie, getSessionEntryFromRequest } = await import('./session');
        const request = new Request('http://localhost/', {
            headers: { cookie: createSidCookie('dangling') },
        });

        const result = await getSessionEntryFromRequest(request);
        expect(result.isOk).toBe(true);
        if (result.isOk) {
            expect(result.value).toBeNull();
        }
        expect(mockRedis.del).toHaveBeenCalledWith('session:dangling');
    });

    it('creates, loads, and deletes auth transactions', async () => {
        const { createAuthTransaction, getAuthTransaction, deleteAuthTransaction } = await import('./session');
        const transaction = {
            codeVerifier: 'verifier',
            redirectTo: '/dashboard',
            state: 'state-1',
        };

        const createResult = await createAuthTransaction(transaction);
        expect(createResult.isOk).toBe(true);
        if (createResult.isErr) {
            return;
        }

        const loaded = await getAuthTransaction(createResult.value);
        expect(loaded.isOk).toBe(true);
        if (loaded.isOk) {
            expect(loaded.value).toEqual(transaction);
        }

        const deleted = await deleteAuthTransaction(createResult.value);
        expect(deleted.isOk).toBe(true);

        const afterDelete = await getAuthTransaction(createResult.value);
        expect(afterDelete.isOk).toBe(true);
        if (afterDelete.isOk) {
            expect(afterDelete.value).toBeNull();
        }
    });

    it('returns Err when Redis operations throw', async () => {
        mockRedis.get.mockRejectedValueOnce(new Error('redis down'));
        const { getSessionById } = await import('./session');

        const result = await getSessionById('any');
        expect(result.isErr).toBe(true);
        if (result.isErr) {
            expect(result.error).toMatchObject({ message: 'redis down' });
        }
    });

    it('destroys a session from the request cookie', async () => {
        const session = sampleSession();
        const { createSession, createSidCookie, destroySession, getSessionById } = await import('./session');

        const createResult = await createSession(session);
        expect(createResult.isOk).toBe(true);
        if (createResult.isErr) {
            return;
        }

        const destroyResult = await destroySession(
            new Request('http://localhost/', {
                headers: { cookie: createSidCookie(createResult.value) },
            }),
        );
        expect(destroyResult.isOk).toBe(true);

        const loaded = await getSessionById(createResult.value);
        expect(loaded.isOk).toBe(true);
        if (loaded.isOk) {
            expect(loaded.value).toBeNull();
        }
    });
});
