import { isErr } from '~/.server/lib/result';
import { createWsTicket } from '~/.server/service/communication';
import { protectedLoader } from '~/.server/service/routeProtection';

export const loader = protectedLoader(async () => {
    const result = await createWsTicket();
    if (isErr(result)) {
        throw result.error;
    }
    return Response.json(result.value);
});
