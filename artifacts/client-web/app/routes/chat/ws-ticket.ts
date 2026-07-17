import { isErr } from '~/.server/lib/result';
import { routeErrorJson } from '~/.server/lib/routeError';
import { createWsTicket } from '~/.server/service/communication';
import { protectedLoader } from '~/.server/service/routeProtection';

export const loader = protectedLoader(async () => {
    const result = await createWsTicket();
    if (isErr(result)) {
        return routeErrorJson(result.error);
    }
    return Response.json(result.value);
});
