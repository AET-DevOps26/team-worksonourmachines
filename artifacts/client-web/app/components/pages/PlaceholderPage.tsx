import { LOREM_PARAGRAPHS } from './lorem';

type PlaceholderPageProps = {
    title: string;
    description?: string;
    paragraphCount?: number;
};

export function PlaceholderPage({ title, description, paragraphCount = 2 }: PlaceholderPageProps) {
    const paragraphs = LOREM_PARAGRAPHS.slice(0, paragraphCount);

    return (
        <div className="mx-auto w-full max-w-3xl px-6 py-12">
            <header className="flex flex-col gap-2 border-b border-border pb-6">
                <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Placeholder</p>
                <h1 className="text-2xl font-semibold tracking-tight text-foreground">{title}</h1>
                {description ? <p className="text-sm leading-relaxed text-muted-foreground">{description}</p> : null}
            </header>

            <div className="mt-8 flex flex-col gap-4 text-sm leading-relaxed text-muted-foreground">
                {paragraphs.map((paragraph) => (
                    <p key={paragraph.slice(0, 24)}>{paragraph}</p>
                ))}
            </div>
        </div>
    );
}
