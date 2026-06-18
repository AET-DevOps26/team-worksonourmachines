import type { ActionFunctionArgs, LoaderFunctionArgs } from 'react-router';
import { runWithRequestAccessToken } from '~/.server/lib/requestAuth';
import { isErr } from '~/.server/lib/result';
import { requireAuthInLoader } from '~/.server/service/auth';
import type { SessionPayload } from '~/.server/service/session';

type ProtectedLoaderArgs = LoaderFunctionArgs & { session: SessionPayload };
type ProtectedActionArgs = ActionFunctionArgs & { session: SessionPayload };

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
