import type { NextFunction, Request, Response } from 'express';

export type AuthUser = {
  sub: string;
  email: string | null;
  name: string | null;
};

export function getBearerSub(req: Request): string | null {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) {
    return null;
  }
  const token = header.slice(7);
  const parts = token.split('.');
  if (parts.length < 2) {
    return null;
  }
  try {
    const payload = JSON.parse(Buffer.from(parts[1] ?? '', 'base64url').toString('utf8')) as {
      sub?: string;
    };
    return payload.sub ?? null;
  } catch {
    return null;
  }
}

export function getAuthUser(req: Request): AuthUser | null {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) {
    return null;
  }
  const token = header.slice(7);
  const parts = token.split('.');
  if (parts.length < 2) {
    return null;
  }
  try {
    const payload = JSON.parse(Buffer.from(parts[1] ?? '', 'base64url').toString('utf8')) as {
      sub?: string;
      email?: string;
      name?: string;
      preferred_username?: string;
    };
    if (!payload.sub) {
      return null;
    }
    return {
      sub: payload.sub,
      email: payload.email ?? payload.preferred_username ?? null,
      name: payload.name ?? null,
    };
  } catch {
    return null;
  }
}

export function requireAuth(req: Request, res: Response, next: NextFunction) {
  const user = getAuthUser(req);
  if (!user) {
    res.status(401).json({ code: 'unauthorized', message: 'Missing or invalid bearer token' });
    return;
  }
  (req as Request & { authUser: AuthUser }).authUser = user;
  next();
}

export function getReqUser(req: Request): AuthUser {
  return (req as Request & { authUser: AuthUser }).authUser;
}
