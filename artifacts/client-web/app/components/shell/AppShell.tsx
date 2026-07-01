import type { ReactNode } from 'react';

import { SiteFooter } from './SiteFooter';
import { SiteHeader } from './SiteHeader';
import type { ShellUser } from './types';

type AppShellProps = {
    children: ReactNode;
    user: ShellUser | null;
};

export function AppShell({ children, user }: AppShellProps) {
    return (
        <div className="flex min-h-svh flex-col bg-background text-foreground">
            <SiteHeader user={user} />
            <main className="flex-1 px-6 py-8">{children}</main>
            <SiteFooter />
        </div>
    );
}
