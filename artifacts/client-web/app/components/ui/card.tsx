import type { ComponentProps } from 'react';
import { cn } from '~/lib/ui/utils';

export function Card({ className, ...props }: ComponentProps<'div'>) {
    return (
        <div
            className={cn('rounded-xl border border-border/60 bg-card/60 p-6 shadow-sm backdrop-blur-sm', className)}
            {...props}
        />
    );
}

export function CardTitle({ className, ...props }: ComponentProps<'h2'>) {
    return <h2 className={cn('text-lg font-semibold tracking-tight', className)} {...props} />;
}

export function CardDescription({ className, ...props }: ComponentProps<'p'>) {
    return <p className={cn('text-sm text-muted-foreground', className)} {...props} />;
}
