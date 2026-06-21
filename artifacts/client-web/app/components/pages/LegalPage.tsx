import { LOREM_PARAGRAPHS } from './lorem';

type LegalPageProps = {
    title: string;
};

export function LegalPage({ title }: LegalPageProps) {
    return (
        <div className="mx-auto w-full max-w-3xl px-6 py-12">
            <header className="flex flex-col gap-2 border-b border-border pb-6">
                <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Legal placeholder</p>
                <h1 className="text-2xl font-semibold tracking-tight text-foreground">{title}</h1>
                <p className="text-sm leading-relaxed text-muted-foreground">
                    This page contains placeholder text only for demo purposes.
                </p>
            </header>

            <div className="mt-8 flex flex-col gap-8">
                {LOREM_PARAGRAPHS.map((paragraph, index) => (
                    <section className="flex flex-col gap-2" key={paragraph.slice(0, 24)}>
                        <h2 className="text-sm font-medium text-foreground">Section {index + 1}</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">{paragraph}</p>
                    </section>
                ))}
            </div>
        </div>
    );
}
