import type { ReactNode } from 'react';

import { cn } from '~/lib/ui/utils';

export const contentMaxWidth = {
    narrow: 'max-w-3xl px-6',
    wide: 'max-w-6xl px-6',
} as const;

type PageContainerProps = {
    children: ReactNode;
    className?: string;
    size?: keyof typeof contentMaxWidth;
};

export function PageContainer({ children, className, size = 'narrow' }: PageContainerProps) {
    return <div className={cn('mx-auto w-full', contentMaxWidth[size], className)}>{children}</div>;
}
