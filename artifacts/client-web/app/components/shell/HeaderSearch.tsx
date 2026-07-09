import { useEffect, useId, useState } from 'react';
import { Link, useFetcher } from 'react-router';
import { Input } from '~/components/ui/input';
import { cn } from '~/lib/ui/utils';

type SearchResult = {
    modules: { id: string; code: string; title: string }[];
    tutors: { id: string; displayName: string; hourlyRate: number }[];
};

export function HeaderSearch() {
    const [query, setQuery] = useState('');
    const [open, setOpen] = useState(false);
    const listboxId = useId();
    const fetcher = useFetcher<SearchResult>();

    useEffect(() => {
        const trimmed = query.trim();
        if (trimmed.length < 2) {
            return;
        }
        const timer = setTimeout(() => {
            fetcher.load(`/search?q=${encodeURIComponent(trimmed)}`);
            setOpen(true);
        }, 300);
        return () => clearTimeout(timer);
    }, [query, fetcher.load]);

    const modules = fetcher.data?.modules ?? [];
    const tutors = fetcher.data?.tutors ?? [];
    const hasResults = modules.length > 0 || tutors.length > 0;
    const showDropdown = open && query.trim().length >= 2;

    return (
        <div className="relative w-full max-w-md">
            <Input
                aria-autocomplete="list"
                aria-controls={listboxId}
                aria-expanded={showDropdown}
                aria-label="Search tutors and modules"
                onBlur={() => setTimeout(() => setOpen(false), 150)}
                onChange={(e) => setQuery(e.target.value)}
                onFocus={() => query.trim().length >= 2 && setOpen(true)}
                placeholder="Search tutors and modules…"
                role="combobox"
                type="search"
                value={query}
            />
            {showDropdown ? (
                <div
                    className="absolute top-full z-50 mt-1 w-full rounded-md border border-border bg-popover p-2 shadow-md"
                    id={listboxId}
                    role="listbox"
                >
                    {fetcher.state === 'loading' ? (
                        <p className="px-2 py-1 text-sm text-muted-foreground">Searching…</p>
                    ) : null}
                    {modules.length > 0 ? (
                        <div className="mb-2">
                            <p className="px-2 py-1 text-xs font-medium text-muted-foreground">Modules</p>
                            {modules.map((m) => (
                                <Link
                                    className="block rounded px-2 py-1.5 text-sm hover:bg-accent"
                                    key={m.id}
                                    onClick={() => setOpen(false)}
                                    role="option"
                                    to={`/modules/${m.code}`}
                                >
                                    <span className="font-medium">{m.code}</span> — {m.title}
                                </Link>
                            ))}
                        </div>
                    ) : null}
                    {tutors.length > 0 ? (
                        <div className="mb-2">
                            <p className="px-2 py-1 text-xs font-medium text-muted-foreground">Tutors</p>
                            {tutors.map((t) => (
                                <Link
                                    className="block rounded px-2 py-1.5 text-sm hover:bg-accent"
                                    key={t.id}
                                    onClick={() => setOpen(false)}
                                    role="option"
                                    to={`/tutors/${t.id}`}
                                >
                                    {t.displayName} · €{t.hourlyRate}/h
                                </Link>
                            ))}
                        </div>
                    ) : null}
                    {!hasResults && fetcher.state !== 'loading' ? (
                        <p className="px-2 py-1 text-sm text-muted-foreground">No results</p>
                    ) : null}
                    <div className="mt-2 border-t border-border pt-2">
                        <Link
                            className={cn('block px-2 py-1 text-sm hover:underline')}
                            onClick={() => setOpen(false)}
                            to={`/modules?q=${encodeURIComponent(query.trim())}`}
                        >
                            View all modules for &quot;{query.trim()}&quot;
                        </Link>
                        <Link
                            className={cn('block px-2 py-1 text-sm hover:underline')}
                            onClick={() => setOpen(false)}
                            to={`/discover?q=${encodeURIComponent(query.trim())}`}
                        >
                            View all tutors for &quot;{query.trim()}&quot;
                        </Link>
                    </div>
                </div>
            ) : null}
        </div>
    );
}
