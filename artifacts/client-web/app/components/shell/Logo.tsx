import { Link } from 'react-router';

import { cn } from '~/lib/ui/utils';

type LogoProps = {
    className?: string;
    to?: string;
};

export function Logo({ className, to = '/' }: LogoProps) {
    const image = (
        <img
            alt="TUtorMatch"
            className={cn('h-8 w-auto', className)}
            height={32}
            src="https://placehold.co/120x32/png?text=TUtorMatch"
            width={120}
        />
    );

    return (
        <Link className="shrink-0" to={to}>
            {image}
        </Link>
    );
}
