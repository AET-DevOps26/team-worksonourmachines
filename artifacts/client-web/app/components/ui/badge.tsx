import { cva, type VariantProps } from 'class-variance-authority';
import type { ComponentProps } from 'react';
import { cn } from '~/lib/ui/utils';

const badgeVariants = cva('inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium', {
    defaultVariants: { variant: 'default' },
    variants: {
        variant: {
            danger: 'bg-red-500/10 text-red-700 dark:text-red-300',
            default: 'bg-primary/10 text-primary',
            outline: 'border border-border text-foreground',
            success: 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-300',
            warning: 'bg-amber-500/10 text-amber-700 dark:text-amber-300',
        },
    },
});

type BadgeProps = ComponentProps<'span'> & VariantProps<typeof badgeVariants>;

export function Badge({ className, variant, ...props }: BadgeProps) {
    return <span className={cn(badgeVariants({ variant }), className)} {...props} />;
}
