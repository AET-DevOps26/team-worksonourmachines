import { isErr } from '~/.server/lib/result';
import { routeErrorJson } from '~/.server/lib/routeError';
import { listModules, listTutors } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';

export const loader = protectedLoader(async ({ request }) => {
    const url = new URL(request.url);
    const q = url.searchParams.get('q')?.trim();
    if (!q) {
        return { modules: [], tutors: [] };
    }

    const [modulesResult, tutorsResult] = await Promise.all([
        listModules({ pageSize: 5, q }),
        listTutors({ pageSize: 5, q }),
    ]);

    if (isErr(modulesResult)) return routeErrorJson(modulesResult.error);
    if (isErr(tutorsResult)) return routeErrorJson(tutorsResult.error);

    return {
        modules: modulesResult.value.items,
        tutors: tutorsResult.value.items,
    };
});
