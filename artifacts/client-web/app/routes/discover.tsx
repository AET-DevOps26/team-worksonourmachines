import { useId } from 'react';
import { Link, useLoaderData, useSearchParams } from 'react-router';
import type {
    SharedMarketplaceLocation,
    SharedMarketplaceTutorSort,
    SharedMarketplaceWeekday,
} from '~/.server/api/server-marketplace/generated';
import { isErr } from '~/.server/lib/result';
import { getModule, listModules, listTutors } from '~/.server/service/marketplace';
import { protectedLoader } from '~/.server/service/routeProtection';
import { formatLocations, TutorLocationFilterOptions, TutorWeekdayFilterFields } from '~/components/tutor';
import { Badge } from '~/components/ui/badge';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Select } from '~/components/ui/select';
import { cn } from '~/lib/ui/utils';

function paginationSearchParams(searchParams: URLSearchParams, page: number) {
    const next = new URLSearchParams();
    for (const key of new Set(searchParams.keys())) {
        if (key === 'page') continue;
        for (const value of searchParams.getAll(key)) {
            next.append(key, value);
        }
    }
    next.set('page', String(page));
    return next.toString();
}

export const loader = protectedLoader(async ({ request }) => {
    const url = new URL(request.url);
    const params = url.searchParams;
    const moduleId = params.get('moduleId') ?? undefined;
    const topicId = params.get('topicId') ?? undefined;
    const q = params.get('q') ?? undefined;
    const page = Number(params.get('page') ?? 1);
    const sort = (params.get('sort') as SharedMarketplaceTutorSort | null) ?? undefined;
    const weekdays = params
        .getAll('weekdays')
        .filter((day): day is SharedMarketplaceWeekday =>
            ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'].includes(day),
        );
    const language = params.get('language') ?? undefined;
    const location = (params.get('location') as SharedMarketplaceLocation | null) ?? undefined;
    const minRate = params.get('minRate') ? Number(params.get('minRate')) : undefined;
    const maxRate = params.get('maxRate') ? Number(params.get('maxRate')) : undefined;
    const minRating = params.get('minRating') ? Number(params.get('minRating')) : undefined;

    const tutorParams: Parameters<typeof listTutors>[0] = { page, pageSize: 12 };
    if (q) tutorParams.q = q;
    if (moduleId) tutorParams.moduleId = moduleId;
    if (topicId) tutorParams.topicId = topicId;
    if (sort) tutorParams.sort = sort;
    if (weekdays.length > 0) tutorParams.weekdays = weekdays;
    if (language) tutorParams.languages = [language];
    if (location) tutorParams.locations = [location];
    if (minRate !== undefined) tutorParams.minRate = minRate;
    if (maxRate !== undefined) tutorParams.maxRate = maxRate;
    if (minRating !== undefined) tutorParams.minRating = minRating;

    const [tutorsResult, modulesResult] = await Promise.all([listTutors(tutorParams), listModules({ pageSize: 100 })]);

    if (isErr(tutorsResult)) throw tutorsResult.error;
    if (isErr(modulesResult)) throw modulesResult.error;

    const selectedModule = moduleId ? modulesResult.value.items.find((m) => m.id === moduleId) : undefined;
    let moduleTopics: { id: string; name: string }[] = [];
    if (selectedModule) {
        const fullModule = await getModule(selectedModule.code);
        if (fullModule.isOk) {
            moduleTopics = fullModule.value.topics.map((t) => ({ id: t.id, name: t.name }));
        }
    }

    return {
        modules: modulesResult.value.items,
        moduleTopics,
        selectedWeekdays: weekdays,
        tutors: tutorsResult.value,
    };
});

export default function DiscoverRoute() {
    const { moduleTopics, modules, selectedWeekdays, tutors } = useLoaderData<typeof loader>();
    const [searchParams] = useSearchParams();
    const totalPages = Math.max(1, Math.ceil(tutors.total / tutors.pageSize));
    const qId = useId();
    const sortId = useId();
    const moduleId = useId();
    const topicId = useId();
    const languageId = useId();
    const locationId = useId();
    const minRateId = useId();
    const maxRateId = useId();
    const minRatingId = useId();

    return (
        <div className="-mx-6 flex flex-col">
            <section className="border-b border-border px-6 py-6">
                <div className="mx-auto max-w-6xl">
                    <h1 className="text-2xl font-semibold tracking-tight text-foreground">Discover tutors</h1>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Search and filter tutors by module, language, location, and budget.
                    </p>
                </div>
            </section>

            <form method="get">
                <section className="sticky top-14 z-30 border-b border-border bg-background/95 px-6 py-3 backdrop-blur">
                    <div className="mx-auto flex max-w-6xl flex-wrap items-end gap-3">
                        <div className="min-w-[200px] flex-1">
                            <Label className="sr-only" htmlFor={qId}>
                                Search
                            </Label>
                            <Input
                                defaultValue={searchParams.get('q') ?? ''}
                                id={qId}
                                name="q"
                                placeholder="Search by name or module…"
                                type="search"
                            />
                        </div>
                        <div className="w-40">
                            <Label className="sr-only" htmlFor={sortId}>
                                Sort by
                            </Label>
                            <Select defaultValue={searchParams.get('sort') ?? 'rating'} id={sortId} name="sort">
                                <option value="rating">Top rated</option>
                                <option value="rate_asc">Rate (low to high)</option>
                                <option value="rate_desc">Rate (high to low)</option>
                                <option value="name">Name</option>
                            </Select>
                        </div>
                        <Button type="submit">Search</Button>
                    </div>
                </section>

                <section className="px-6 py-6">
                    <div className="mx-auto flex max-w-6xl gap-8">
                        <aside className="hidden w-56 shrink-0 lg:block">
                            <div className="sticky top-[7.75rem] flex flex-col gap-5">
                                <div>
                                    <Label htmlFor={moduleId}>Module</Label>
                                    <Select
                                        className="mt-1.5"
                                        defaultValue={searchParams.get('moduleId') ?? ''}
                                        id={moduleId}
                                        name="moduleId"
                                    >
                                        <option value="">All modules</option>
                                        {modules.map((m) => (
                                            <option key={m.id} value={m.id}>
                                                {m.code}
                                            </option>
                                        ))}
                                    </Select>
                                </div>

                                <div>
                                    <Label htmlFor={topicId}>Topic</Label>
                                    <Select
                                        className="mt-1.5"
                                        defaultValue={searchParams.get('topicId') ?? ''}
                                        id={topicId}
                                        name="topicId"
                                    >
                                        <option value="">All topics</option>
                                        {moduleTopics.map((t) => (
                                            <option key={t.id} value={t.id}>
                                                {t.name}
                                            </option>
                                        ))}
                                    </Select>
                                </div>

                                <div>
                                    <Label htmlFor={languageId}>Language</Label>
                                    <Select
                                        className="mt-1.5"
                                        defaultValue={searchParams.get('language') ?? ''}
                                        id={languageId}
                                        name="language"
                                    >
                                        <option value="">Any</option>
                                        <option value="German">German</option>
                                        <option value="English">English</option>
                                    </Select>
                                </div>

                                <div>
                                    <Label htmlFor={locationId}>Location</Label>
                                    <Select
                                        className="mt-1.5"
                                        defaultValue={searchParams.get('location') ?? ''}
                                        id={locationId}
                                        name="location"
                                    >
                                        <option value="">Any</option>
                                        <TutorLocationFilterOptions />
                                    </Select>
                                </div>

                                <div>
                                    <Label>Available on</Label>
                                    <p className="mt-1 text-xs text-muted-foreground">Any selected day</p>
                                    <div className="mt-2">
                                        <TutorWeekdayFilterFields selected={selectedWeekdays} />
                                    </div>
                                </div>

                                <div className="grid grid-cols-2 gap-2">
                                    <div>
                                        <Label htmlFor={minRateId}>Min €/h</Label>
                                        <Input
                                            className="mt-1.5"
                                            defaultValue={searchParams.get('minRate') ?? ''}
                                            id={minRateId}
                                            min={0}
                                            name="minRate"
                                            type="number"
                                        />
                                    </div>
                                    <div>
                                        <Label htmlFor={maxRateId}>Max €/h</Label>
                                        <Input
                                            className="mt-1.5"
                                            defaultValue={searchParams.get('maxRate') ?? ''}
                                            id={maxRateId}
                                            min={0}
                                            name="maxRate"
                                            type="number"
                                        />
                                    </div>
                                </div>

                                <div>
                                    <Label htmlFor={minRatingId}>Min rating</Label>
                                    <Input
                                        className="mt-1.5"
                                        defaultValue={searchParams.get('minRating') ?? ''}
                                        id={minRatingId}
                                        max={5}
                                        min={0}
                                        name="minRating"
                                        step={0.1}
                                        type="number"
                                    />
                                </div>

                                <Button className="w-full" type="submit">
                                    Apply filters
                                </Button>
                            </div>
                        </aside>

                        <main className="min-w-0 flex-1">
                            <p className="mb-4 text-sm text-muted-foreground">
                                {tutors.total} tutor{tutors.total === 1 ? '' : 's'} found
                            </p>

                            {tutors.items.length === 0 ? (
                                <Card>
                                    <CardDescription>
                                        No tutors match your filters. Try broadening your search.
                                    </CardDescription>
                                </Card>
                            ) : (
                                <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                                    {tutors.items.map((tutor) => (
                                        <Link key={tutor.id} to={`/tutors/${tutor.id}`}>
                                            <Card className="h-full transition-colors hover:border-primary/40">
                                                <CardTitle className="text-base">{tutor.displayName}</CardTitle>
                                                <CardDescription className="mt-1">
                                                    €{tutor.hourlyRate}/h · ★ {tutor.ratingSummary.average.toFixed(1)}
                                                </CardDescription>
                                                <div className="mt-3 flex flex-wrap gap-1">
                                                    {tutor.coverages.map((c) => (
                                                        <Badge key={c.moduleId} variant="outline">
                                                            {c.moduleCode}
                                                        </Badge>
                                                    ))}
                                                </div>
                                                <p className="mt-2 text-xs text-muted-foreground">
                                                    {tutor.languages.join(', ')} · {formatLocations(tutor.locations)}
                                                </p>
                                            </Card>
                                        </Link>
                                    ))}
                                </div>
                            )}

                            {tutors.total > tutors.pageSize ? (
                                <div className="mt-6 flex items-center justify-between gap-4">
                                    {tutors.page > 1 ? (
                                        <Link
                                            className={cn(buttonVariants({ variant: 'outline' }))}
                                            to={`?${paginationSearchParams(searchParams, tutors.page - 1)}`}
                                        >
                                            Previous
                                        </Link>
                                    ) : (
                                        <span />
                                    )}
                                    <p className="text-sm text-muted-foreground">
                                        Page {tutors.page} of {totalPages}
                                    </p>
                                    {tutors.page * tutors.pageSize < tutors.total ? (
                                        <Link
                                            className={cn(buttonVariants({ variant: 'outline' }))}
                                            to={`?${paginationSearchParams(searchParams, tutors.page + 1)}`}
                                        >
                                            Next
                                        </Link>
                                    ) : (
                                        <span />
                                    )}
                                </div>
                            ) : null}
                        </main>
                    </div>
                </section>
            </form>
        </div>
    );
}
