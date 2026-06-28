import { Link } from 'react-router';

import { cn } from '~/lib/ui/utils';

type LogoProps = {
    className?: string;
    linkToHome?: boolean;
};

export function Logo({ className, linkToHome = true }: LogoProps) {
    const image = (
        <img
            alt="TUtorMatch"
            className={cn('h-8 w-auto', className)}
            height={32}
            src="https://placehold.co/120x32/png?text=TUtorMatch"
            width={120}
        />
    );

    if (!linkToHome) {
        return image;
    }

    return (
        <Link className="shrink-0" to="/">
            {image}
        </Link>
    );
}
