import { useId, useState } from 'react';
import { cn } from '~/lib/ui/utils';

type StudyFocusRatingProps = {
    defaultValue?: number;
    id?: string;
    name: string;
};

const MAX_STARS = 5;

export function StudyFocusRating({ defaultValue = 3, id, name }: StudyFocusRatingProps) {
    const generatedId = useId();
    const groupId = id ?? generatedId;
    const [value, setValue] = useState(defaultValue);

    return (
        <div className="flex flex-col gap-1">
            <input name={name} type="hidden" value={value} />
            <div
                aria-label={`${value} out of ${MAX_STARS} stars`}
                className="flex items-center gap-1"
                id={groupId}
                role="radiogroup"
            >
                {Array.from({ length: MAX_STARS }, (_, index) => {
                    const starValue = index + 1;
                    const filled = starValue <= value;
                    const inputId = `${groupId}-star-${starValue}`;

                    return (
                        <label
                            className={cn(
                                'cursor-pointer rounded p-0.5 text-xl leading-none transition-colors hover:scale-110',
                                filled ? 'text-amber-500' : 'text-muted-foreground/40 hover:text-amber-400/60',
                            )}
                            htmlFor={inputId}
                            key={starValue}
                        >
                            <input
                                checked={value === starValue}
                                className="sr-only"
                                id={inputId}
                                name={`${name}_picker`}
                                onChange={() => setValue(starValue)}
                                type="radio"
                                value={starValue}
                            />
                            ★
                        </label>
                    );
                })}
            </div>
        </div>
    );
}

export function StudyFocusStarsDisplay({ value }: { value: number }) {
    return (
        <span aria-label={`${value} out of ${MAX_STARS} stars`} className="inline-flex items-center gap-0.5" role="img">
            {Array.from({ length: MAX_STARS }, (_, index) => {
                const starValue = index + 1;
                const filled = starValue <= value;

                return (
                    <span
                        aria-hidden
                        className={cn('text-base leading-none', filled ? 'text-amber-500' : 'text-muted-foreground/30')}
                        key={starValue}
                    >
                        ★
                    </span>
                );
            })}
        </span>
    );
}
