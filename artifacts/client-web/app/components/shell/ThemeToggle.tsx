'use client';

import { Moon01Icon, Sun01Icon } from '@hugeicons/core-free-icons';
import { HugeiconsIcon } from '@hugeicons/react';

import { Button } from '~/components/ui/button';
import { useTheme } from '~/lib/ui/use-theme';

export function ThemeToggle() {
    const { theme, toggleTheme } = useTheme();

    return (
        <Button
            aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
            onClick={toggleTheme}
            size="icon"
            type="button"
            variant="ghost"
        >
            {theme === 'dark' ? (
                <HugeiconsIcon icon={Sun01Icon} strokeWidth={2} />
            ) : (
                <HugeiconsIcon icon={Moon01Icon} strokeWidth={2} />
            )}
        </Button>
    );
}
