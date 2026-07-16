import { useId } from 'react';
import { Link } from 'react-router';

import { cn } from '~/lib/ui/utils';

type LogoProps = {
    className?: string;
    to?: string;
};

export function Logo({ className, to = '/' }: LogoProps) {
    const titleId = useId();

    const image = (
        <svg
            aria-labelledby={titleId}
            className={cn('h-8 w-auto', className)}
            role="img"
            viewBox="0 0 118 21"
            xmlns="http://www.w3.org/2000/svg"
        >
            <title id={titleId}>TUtorMatch</title>
            <text
                fontFamily="ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
                fontSize="20"
                fontWeight="600"
                letterSpacing="-0.35"
                textAnchor="middle"
                x="59"
                y="18"
            >
                <tspan className="fill-[#1597a3] dark:fill-primary">TU</tspan>
                <tspan className="fill-[#1b2f3a] dark:fill-foreground">tor</tspan>
                <tspan className="fill-[#1597a3] dark:fill-primary">M</tspan>
                <tspan className="fill-[#1b2f3a] dark:fill-foreground">atch</tspan>
            </text>
        </svg>
    );

    return (
        <Link className="shrink-0" to={to}>
            {image}
        </Link>
    );
}
