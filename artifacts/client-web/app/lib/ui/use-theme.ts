import { useCallback, useEffect, useState } from 'react';

const THEME_STORAGE_KEY = 'theme';

type Theme = 'dark' | 'light';

function getSystemTheme(): Theme {
    if (typeof window === 'undefined') {
        return 'light';
    }

    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

function getStoredTheme(): Theme {
    if (typeof window === 'undefined') {
        return 'light';
    }

    const stored = localStorage.getItem(THEME_STORAGE_KEY);
    if (stored === 'light' || stored === 'dark') {
        return stored;
    }

    return getSystemTheme();
}

function applyTheme(theme: Theme) {
    document.documentElement.classList.toggle('dark', theme === 'dark');
}

export function useTheme() {
    const [theme, setThemeState] = useState<Theme>('light');

    useEffect(() => {
        const initial = getStoredTheme();
        applyTheme(initial);
        setThemeState(initial);
    }, []);

    const setTheme = useCallback((next: Theme) => {
        localStorage.setItem(THEME_STORAGE_KEY, next);
        applyTheme(next);
        setThemeState(next);
    }, []);

    const toggleTheme = useCallback(() => {
        setTheme(theme === 'dark' ? 'light' : 'dark');
    }, [setTheme, theme]);

    return { setTheme, theme, toggleTheme };
}
