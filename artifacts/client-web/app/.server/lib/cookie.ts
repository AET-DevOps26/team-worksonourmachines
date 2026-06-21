import { parse, serialize } from 'cookie';
import { env } from '~/.server/lib/env';

type CookieOptions = {
    maxAge: number;
    name: string;
    value: string;
};

export function getCookieValue(request: Request, cookieName: string): string | null {
    const cookies = parse(request.headers.get('cookie') ?? '');
    const cookie = cookies[cookieName];
    return typeof cookie === 'string' && cookie.length > 0 ? cookie : null;
}

export function serializeCookie({ name, value, maxAge }: CookieOptions): string {
    return serialize(name, value, {
        httpOnly: true,
        maxAge,
        path: '/',
        sameSite: 'lax',
        secure: env.isSecureCookies,
    });
}
