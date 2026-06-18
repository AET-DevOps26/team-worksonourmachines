const footerLinks = [
    { href: '/about', label: 'About' },
    { href: '/help', label: 'Help' },
    { href: '/contact', label: 'Contact' },
    { href: '/become-a-tutor', label: 'Become a tutor' },
] as const;

export function SiteFooter() {
    const year = new Date().getFullYear();

    return (
        <footer className="border-t border-border bg-background">
            <div className="mx-auto max-w-6xl px-6 py-10">
                <div className="grid gap-8 sm:grid-cols-3">
                    <div className="flex flex-col gap-2">
                        <p className="text-sm font-medium text-foreground">TUtorMatch</p>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            TUtorMatch connects TUM students with qualified tutors for personalized learning.
                        </p>
                    </div>

                    <div className="flex flex-col gap-2">
                        <p className="text-sm font-medium text-foreground">Legal</p>
                        <nav aria-label="Legal" className="flex flex-col gap-1">
                            <a className="text-sm text-muted-foreground hover:text-foreground" href="/terms">
                                Terms of Service
                            </a>
                            <a className="text-sm text-muted-foreground hover:text-foreground" href="/privacy">
                                Privacy Policy
                            </a>
                        </nav>
                    </div>

                    <div className="flex flex-col gap-2">
                        <p className="text-sm font-medium text-foreground">Links</p>
                        <nav aria-label="Footer links" className="flex flex-col gap-1">
                            {footerLinks.map((link) => (
                                <a
                                    className="text-sm text-muted-foreground hover:text-foreground"
                                    href={link.href}
                                    key={link.label}
                                >
                                    {link.label}
                                </a>
                            ))}
                        </nav>
                    </div>
                </div>

                <p className="mt-8 border-t border-border pt-6 text-sm text-muted-foreground">
                    © {year} TUtorMatch. All rights reserved.
                </p>
            </div>
        </footer>
    );
}
