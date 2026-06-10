import { cn } from '../../../lib/cn';

export function Button({ className }: { className?: string }) {
    return <button className={cn('btn', className)} type="button" />;
}
