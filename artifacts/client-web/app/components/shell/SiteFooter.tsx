import { Link } from 'react-router';

import { cn } from '~/lib/ui/utils';

import { footerSections } from './nav';
import { contentMaxWidth } from './PageContainer';

export function SiteFooter() {
    const year = new Date().getFullYear();

    return (
        <footer className="border-t border-border/60 bg-card/30">
            <div className={cn('mx-auto py-10', contentMaxWidth.wide)}>
                <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
                    <div className="flex flex-col gap-2 sm:col-span-2 lg:col-span-1">
                        <p className="text-sm font-medium text-foreground">TUtorMatch</p>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            TUtorMatch helps students find tutors who know their course modules — personalized,
                            affordable, and independent of any university.
                        </p>
                    </div>

                    {footerSections.map((section) => (
                        <div className="flex flex-col gap-2" key={section.title}>
                            <p className="text-sm font-medium text-foreground">{section.title}</p>
                            <nav aria-label={section.title} className="flex flex-col gap-1">
                                {section.links.map((link) => (
                                    <Link
                                        className="text-sm text-muted-foreground transition-colors hover:text-primary"
                                        key={link.href}
                                        to={link.href}
                                    >
                                        {link.label}
                                    </Link>
                                ))}
                            </nav>
                        </div>
                    ))}
                </div>

                <p className="mt-8 border-t border-border pt-6 text-sm text-muted-foreground">
                    © {year} TUtorMatch. All rights reserved.
                </p>
            </div>
        </footer>
    );
}
