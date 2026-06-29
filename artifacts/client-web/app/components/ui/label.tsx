import type { ComponentProps } from 'react';
import { cn } from '~/lib/ui/utils';

export function Label({ className, ...props }: ComponentProps<'label'>) {
    // biome-ignore lint/a11y/noLabelWithoutControl: htmlFor is provided by callers when associating with a control
    return <label className={cn('text-sm font-medium text-foreground', className)} {...props} />;
}
