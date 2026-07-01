import type { ActionFunctionArgs, LoaderFunctionArgs } from 'react-router';
import { HttpStatusCode } from '~/.server/lib/http';
import { runWithRequestAccessToken } from '~/.server/lib/requestAuth';
import { isErr } from '~/.server/lib/result';
import { requireAuthInLoader } from '~/.server/service/auth';
import type { SessionPayload } from '~/.server/service/session';

type ProtectedLoaderArgs = LoaderFunctionArgs & { session: SessionPayload };
type ProtectedActionArgs = ActionFunctionArgs & { session: SessionPayload };

function hasRole(session: SessionPayload, role: string): boolean {
    return session.user.roles.includes(role);
}

export function forbiddenResponse(): Response {
    return new Response('Forbidden', { status: HttpStatusCode.Forbidden });
}

export function protectedLoader<T>(
    loader: (args: ProtectedLoaderArgs) => Promise<T> | T,
): (args: LoaderFunctionArgs) => Promise<T> {
    return async (args: LoaderFunctionArgs) => {
        const sessionResult = await requireAuthInLoader(args.request);
        if (isErr(sessionResult)) {
            throw sessionResult.error;
        }

        return runWithRequestAccessToken(sessionResult.value.accessToken, () =>
            loader({ ...args, session: sessionResult.value }),
        );
    };
}

export function protectedAction<T>(
    action: (args: ProtectedActionArgs) => Promise<T> | T,
): (args: ActionFunctionArgs) => Promise<T> {
    return async (args: ActionFunctionArgs) => {
        const sessionResult = await requireAuthInLoader(args.request);
        if (isErr(sessionResult)) {
            throw sessionResult.error;
        }

        return runWithRequestAccessToken(sessionResult.value.accessToken, () =>
            action({ ...args, session: sessionResult.value }),
        );
    };
}

export function roleProtectedLoader<T>(
    role: string,
    loader: (args: ProtectedLoaderArgs) => Promise<T> | T,
): (args: LoaderFunctionArgs) => Promise<T> {
    return protectedLoader(async (args) => {
        if (!hasRole(args.session, role)) {
            throw forbiddenResponse();
        }
        return loader(args);
    });
}

export function roleProtectedAction<T>(
    role: string,
    action: (args: ProtectedActionArgs) => Promise<T> | T,
): (args: ActionFunctionArgs) => Promise<T> {
    return protectedAction(async (args) => {
        if (!hasRole(args.session, role)) {
            throw forbiddenResponse();
        }
        return action(args);
    });
}
