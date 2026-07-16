import { PageContainer } from '~/components/shell';

import { LOREM_PARAGRAPHS } from './lorem';

type PlaceholderPageProps = {
    title: string;
    description?: string;
    paragraphCount?: number;
};

export function PlaceholderPage({ title, description, paragraphCount = 2 }: PlaceholderPageProps) {
    const paragraphs = LOREM_PARAGRAPHS.slice(0, paragraphCount);

    return (
        <PageContainer>
            <header className="flex flex-col gap-3 rounded-xl border border-border/60 bg-card/60 p-6 shadow-sm backdrop-blur-sm">
                <p className="w-fit rounded-full bg-accent px-3 py-1 text-xs font-medium text-accent-foreground">
                    Placeholder
                </p>
                <h1 className="text-3xl font-semibold tracking-tight text-foreground">{title}</h1>
                {description ? <p className="text-base leading-relaxed text-muted-foreground">{description}</p> : null}
            </header>

            <div className="mt-8 flex flex-col gap-4 rounded-xl border border-border/40 bg-muted/30 p-6 text-sm leading-relaxed text-muted-foreground">
                {paragraphs.map((paragraph) => (
                    <p key={paragraph.slice(0, 24)}>{paragraph}</p>
                ))}
            </div>
        </PageContainer>
    );
}
