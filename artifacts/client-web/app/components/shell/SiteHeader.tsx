import { Fragment } from 'react';
import { Form, Link } from 'react-router';
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
import { cn } from '~/lib/ui/utils';
import { HeaderSearch } from './HeaderSearch';
import { Logo } from './Logo';
import { appNavLinks, getProfileMenuGroups, publicNavLinks } from './nav';
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
    const profileMenuGroups = user ? getProfileMenuGroups(user.roles) : [];

    return (
        <header className="sticky top-0 z-40 border-b border-border/60 bg-background/80 backdrop-blur-md">
            <div className="mx-auto grid h-14 max-w-6xl grid-cols-[auto_minmax(0,1fr)_auto] items-center gap-4 px-6">
                <Logo to={user ? '/dashboard' : '/'} />

                <div className="flex min-w-0 items-center justify-center">
                    {user ? (
                        <div className="hidden w-full justify-center sm:flex">
                            <HeaderSearch />
                        </div>
                    ) : (
                        <nav aria-label="Main" className="hidden items-center gap-2 sm:flex">
                            {publicNavLinks.map((link) => (
                                <Link
                                    className={cn(buttonVariants({ size: 'sm', variant: 'ghost' }))}
                                    key={link.href}
                                    to={link.href}
                                >
                                    {link.label}
                                </Link>
                            ))}
                        </nav>
                    )}
                </div>

                <div className="flex shrink-0 items-center gap-2">
                    {user ? (
                        <>
                            <nav aria-label="Main" className="hidden items-center gap-2 md:flex">
                                {appNavLinks.map((link) => (
                                    <Link
                                        className={cn(buttonVariants({ size: 'sm', variant: 'ghost' }))}
                                        key={link.href}
                                        to={link.href}
                                    >
                                        {link.label}
                                    </Link>
                                ))}
                            </nav>

                            <Link className={cn(buttonVariants({ size: 'sm', variant: 'ghost' }))} to="/chat">
                                Chat
                            </Link>

                            <ThemeToggle />

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

                                    {profileMenuGroups.map((group, groupIndex) => (
                                        <Fragment key={group.label ?? `group-${groupIndex}`}>
                                            <DropdownMenuSeparator />
                                            <DropdownMenuGroup>
                                                {group.label ? (
                                                    <DropdownMenuLabel>{group.label}</DropdownMenuLabel>
                                                ) : null}
                                                {group.links.map((link) => (
                                                    <DropdownMenuItem
                                                        key={link.href}
                                                        render={<Link to={link.href}>{link.label}</Link>}
                                                    />
                                                ))}
                                            </DropdownMenuGroup>
                                        </Fragment>
                                    ))}

                                    <DropdownMenuSeparator />
                                    <DropdownMenuGroup>
                                        <Form action="/auth/logout" method="post">
                                            <DropdownMenuItem
                                                nativeButton
                                                render={<button type="submit">Sign out</button>}
                                            />
                                        </Form>
                                    </DropdownMenuGroup>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </>
                    ) : (
                        <>
                            <ThemeToggle />
                            <Link className={cn(buttonVariants())} to="/login">
                                Log in
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
