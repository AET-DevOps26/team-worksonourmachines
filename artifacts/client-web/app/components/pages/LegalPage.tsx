import { PageContainer } from '~/components/shell';

import { LOREM_PARAGRAPHS } from './lorem';

type LegalPageProps = {
    title: string;
};

export function LegalPage({ title }: LegalPageProps) {
    return (
        <PageContainer className="py-4">
            <header className="flex flex-col gap-3 rounded-xl border border-border/60 bg-card/60 p-6 shadow-sm backdrop-blur-sm">
                <p className="w-fit rounded-full bg-secondary px-3 py-1 text-xs font-medium text-secondary-foreground">
                    Legal placeholder
                </p>
                <h1 className="text-3xl font-semibold tracking-tight text-foreground">{title}</h1>
                <p className="text-base leading-relaxed text-muted-foreground">
                    This page contains placeholder text only for demo purposes.
                </p>
            </header>

            <div className="mt-8 flex flex-col gap-6">
                {LOREM_PARAGRAPHS.map((paragraph, index) => (
                    <section
                        className="flex flex-col gap-2 rounded-lg border border-border/40 bg-muted/20 p-5"
                        key={paragraph.slice(0, 24)}
                    >
                        <h2 className="text-sm font-medium text-primary">Section {index + 1}</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">{paragraph}</p>
                    </section>
                ))}
            </div>
        </PageContainer>
    );
}
