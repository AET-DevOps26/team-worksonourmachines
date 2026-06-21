import { Form } from 'react-router';
import { Avatar, AvatarFallback } from '~/components/ui/avatar';
import { Button, buttonVariants } from '~/components/ui/button';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '~/components/ui/dropdown-menu';
import { Input } from '~/components/ui/input';
import { cn } from '~/lib/ui/utils';

import { ThemeToggle } from './ThemeToggle';
import type { ShellUser } from './types';

function getInitials(user: ShellUser) {
    const displayName = user.name ?? user.username;
    const parts = displayName.trim().split(/\s+/);

    if (parts.length >= 2) {
        return `${parts[0]?.[0] ?? ''}${parts[1]?.[0] ?? ''}`.toUpperCase();
    }

    return displayName.slice(0, 2).toUpperCase();
}

type SiteHeaderProps = {
    user: ShellUser | null;
};

export function SiteHeader({ user }: SiteHeaderProps) {
    return (
        <header className="border-b border-border bg-background">
            <div className="mx-auto flex h-14 max-w-6xl items-center gap-4 px-6">
                <a className="shrink-0" href="/">
                    <img
                        alt="TUtorMatch"
                        className="h-8 w-auto"
                        height={32}
                        src="https://placehold.co/120x32/png?text=TUtorMatch"
                        width={120}
                    />
                </a>

                {user ? (
                    <div className="mx-auto hidden w-full max-w-md sm:block">
                        <Input aria-label="Search tutors" placeholder="Search tutors…" type="search" />
                    </div>
                ) : (
                    <div className="flex-1" />
                )}

                <div className="flex shrink-0 items-center gap-1">
                    <ThemeToggle />

                    {user ? (
                        <>
                            <a className={cn(buttonVariants({ variant: 'ghost' }))} href="/chat">
                                Chat
                            </a>

                            <DropdownMenu>
                                <DropdownMenuTrigger
                                    render={
                                        <Button
                                            aria-label="Open profile menu"
                                            className="rounded-full"
                                            size="icon"
                                            variant="ghost"
                                        />
                                    }
                                >
                                    <Avatar size="sm">
                                        <AvatarFallback>{getInitials(user)}</AvatarFallback>
                                    </Avatar>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent align="end" className="w-56">
                                    <DropdownMenuGroup>
                                        <DropdownMenuLabel>
                                            <div className="flex flex-col gap-0.5">
                                                <span className="font-medium text-foreground">
                                                    {user.name ?? user.username}
                                                </span>
                                                {user.email ? (
                                                    <span className="text-xs text-muted-foreground">{user.email}</span>
                                                ) : null}
                                            </div>
                                        </DropdownMenuLabel>
                                    </DropdownMenuGroup>
                                    <DropdownMenuSeparator />
                                    <Form action="/auth/logout" method="post">
                                        <DropdownMenuItem render={<button type="submit">Sign out</button>} />
                                    </Form>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </>
                    ) : (
                        <a className={cn(buttonVariants())} href="/login">
                            Log in
                        </a>
                    )}
                </div>
            </div>
        </header>
    );
}
