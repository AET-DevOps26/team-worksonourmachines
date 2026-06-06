import crypto from 'node:crypto';
import { createClient, type RedisClientType } from 'redis';
import { getCookieValue, serializeCookie } from '~/.server/lib/cookie';
import { env } from '~/.server/lib/env';
import { logger } from '~/.server/lib/logger';
import { type AsyncResult, err, ok } from '~/.server/lib/result';

const SESSION_TTL_SECONDS = 60 * 60 * 24 * 7;
const AUTH_TXN_TTL_SECONDS = 600;

const SESSION_NAMESPACE = 'session';
const AUTH_TXN_NAMESPACE = 'auth_txn';
const SID_COOKIE_NAME = 'sid';
const SID_TXN_COOKIE_NAME = 'sid_txn';

let redisClient: RedisClientType | null = null;
let redisConnectPromise: Promise<Error | undefined> | null = null;

async function getRedisClient(): AsyncResult<RedisClientType> {
    if (redisClient) {
        return ok(redisClient);
    }

    if (!redisConnectPromise) {
        redisClient = createClient({
            url: env.get('REDIS_URL'),
        });

        redisClient.on('error', (error) => {
            logger.error('Redis connection error', { error, source: 'session-store' });
        });

        redisConnectPromise = redisClient.connect().then(
            () => undefined,
            (error) => {
                redisConnectPromise = null;
                redisClient = null;
                return error instanceof Error ? error : new Error(String(error));
            },
        );
    }

    const connectError = await redisConnectPromise;
    if (connectError) {
        return err(connectError);
    }

    if (!redisClient) {
        return err(new Error('Redis connection failed'));
    }

    return ok(redisClient);
}

async function withRedis<T>(fn: (client: RedisClientType) => Promise<T>): AsyncResult<T> {
    const clientResult = await getRedisClient();
    if (clientResult.isErr) {
        return clientResult;
    }

    try {
        return ok(await fn(clientResult.value));
    } catch (error) {
        return err(error instanceof Error ? error : new Error(String(error)));
    }
}

export type SessionUser = {
    readonly sub: string;
    readonly email: string | null;
    readonly name: string | null;
    readonly preferredUsername: string | null;
    readonly roles: readonly string[];
};

export type SessionPayload = {
    readonly accessToken: string;
    readonly expiresAt: number;
    readonly idToken: string | undefined;
    readonly refreshToken: string | undefined;
    readonly user: SessionUser;
};

export type AuthPayload = SessionPayload;

type SessionEntry = {
    readonly id: string;
    readonly session: SessionPayload;
};

export type AuthTransaction = {
    readonly codeVerifier: string;
    readonly redirectTo: string;
    readonly state: string;
};

function getSessionKey(sessionId: string): string {
    return `${SESSION_NAMESPACE}:${sessionId}`;
}

function getAuthTxnKey(txnId: string): string {
    return `${AUTH_TXN_NAMESPACE}:${txnId}`;
}

export function createSidCookie(sessionId: string): string {
    return serializeCookie({ maxAge: SESSION_TTL_SECONDS, name: SID_COOKIE_NAME, value: sessionId });
}

export function clearSidCookie(): string {
    return serializeCookie({ maxAge: 0, name: SID_COOKIE_NAME, value: '' });
}

export function getSidFromRequest(request: Request): string | null {
    return getCookieValue(request, SID_COOKIE_NAME);
}

export function createSidTxnCookie(txnId: string): string {
    return serializeCookie({ maxAge: AUTH_TXN_TTL_SECONDS, name: SID_TXN_COOKIE_NAME, value: txnId });
}

export function clearSidTxnCookie(): string {
    return serializeCookie({ maxAge: 0, name: SID_TXN_COOKIE_NAME, value: '' });
}

export function getSidTxnFromRequest(request: Request): string | null {
    return getCookieValue(request, SID_TXN_COOKIE_NAME);
}

export async function createAuthTransaction(transaction: AuthTransaction): AsyncResult<string> {
    return withRedis(async (client) => {
        const txnId = crypto.randomUUID();
        await client.set(getAuthTxnKey(txnId), JSON.stringify(transaction), { EX: AUTH_TXN_TTL_SECONDS });
        return txnId;
    });
}

export async function getAuthTransaction(txnId: string): AsyncResult<AuthTransaction | null> {
    return withRedis(async (client) => {
        const raw = await client.get(getAuthTxnKey(txnId));
        if (!raw) {
            return null;
        }

        try {
            const parsed = JSON.parse(raw) as AuthTransaction;
            if (
                typeof parsed.codeVerifier !== 'string' ||
                typeof parsed.redirectTo !== 'string' ||
                typeof parsed.state !== 'string'
            ) {
                return null;
            }

            return parsed;
        } catch {
            return null;
        }
    });
}

export async function deleteAuthTransaction(txnId: string): AsyncResult<void> {
    return withRedis(async (client) => {
        await client.del(getAuthTxnKey(txnId));
    });
}

export async function createSession(payload: SessionPayload): AsyncResult<string> {
    return withRedis(async (client) => {
        const sessionId = crypto.randomUUID();
        await client.set(getSessionKey(sessionId), JSON.stringify(payload), { EX: SESSION_TTL_SECONDS });
        return sessionId;
    });
}

export async function createSessionFromAuthPayload(authPayload: AuthPayload): AsyncResult<string> {
    return createSession(authPayload);
}

export async function updateSession(sessionId: string, payload: SessionPayload): AsyncResult<void> {
    return withRedis(async (client) => {
        await client.set(getSessionKey(sessionId), JSON.stringify(payload), { EX: SESSION_TTL_SECONDS });
    });
}

export async function getSessionById(sessionId: string): AsyncResult<SessionPayload | null> {
    return withRedis(async (client) => {
        const raw = await client.get(getSessionKey(sessionId));
        if (!raw) {
            return null;
        }

        try {
            const parsed = JSON.parse(raw) as SessionPayload;
            if (
                typeof parsed.accessToken !== 'string' ||
                typeof parsed.expiresAt !== 'number' ||
                typeof parsed.user !== 'object' ||
                parsed.user === null ||
                typeof parsed.user.sub !== 'string' ||
                (parsed.user.email !== null && typeof parsed.user.email !== 'string') ||
                (parsed.user.name !== null && typeof parsed.user.name !== 'string') ||
                (parsed.user.preferredUsername !== null && typeof parsed.user.preferredUsername !== 'string') ||
                !Array.isArray(parsed.user.roles) ||
                !parsed.user.roles.every((role) => typeof role === 'string')
            ) {
                return null;
            }

            if (parsed.idToken !== undefined && typeof parsed.idToken !== 'string') {
                return null;
            }

            if (parsed.refreshToken !== undefined && typeof parsed.refreshToken !== 'string') {
                return null;
            }

            return parsed;
        } catch {
            return null;
        }
    });
}

export async function getSessionEntryFromRequest(request: Request): AsyncResult<SessionEntry | null> {
    const sessionId = getSidFromRequest(request);
    if (!sessionId) {
        return ok(null);
    }

    const sessionResult = await getSessionById(sessionId);
    if (sessionResult.isErr) {
        return sessionResult;
    }

    if (!sessionResult.value) {
        const deleteResult = await deleteSession(sessionId);
        if (deleteResult.isErr) {
            return deleteResult;
        }

        return ok(null);
    }

    return ok({ id: sessionId, session: sessionResult.value });
}

export async function getSession(request: Request): AsyncResult<SessionPayload | null> {
    const entryResult = await getSessionEntryFromRequest(request);
    if (entryResult.isErr) {
        return entryResult;
    }

    return ok(entryResult.value?.session ?? null);
}

export async function getSessionUser(request: Request): AsyncResult<SessionUser | null> {
    const sessionResult = await getSession(request);
    if (sessionResult.isErr) {
        return sessionResult;
    }

    return ok(sessionResult.value?.user ?? null);
}

export async function deleteSession(sessionId: string): AsyncResult<void> {
    return withRedis(async (client) => {
        await client.del(getSessionKey(sessionId));
    });
}

export async function destroySession(request: Request): AsyncResult<void> {
    const sessionId = getSidFromRequest(request);
    if (!sessionId) {
        return ok(undefined);
    }

    return deleteSession(sessionId);
}
