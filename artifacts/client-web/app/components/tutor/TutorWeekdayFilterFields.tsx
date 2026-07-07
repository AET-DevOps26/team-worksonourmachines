import type { SharedMarketplaceWeekday } from '~/.server/api/server-marketplace/generated';

const WEEKDAYS: SharedMarketplaceWeekday[] = [
    'monday',
    'tuesday',
    'wednesday',
    'thursday',
    'friday',
    'saturday',
    'sunday',
];

function formatWeekday(day: SharedMarketplaceWeekday) {
    return day.charAt(0).toUpperCase() + day.slice(1);
}

type TutorWeekdayFilterFieldsProps = {
    selected: readonly SharedMarketplaceWeekday[];
};

export function TutorWeekdayFilterFields({ selected }: TutorWeekdayFilterFieldsProps) {
    return (
        <div className="flex flex-wrap gap-3">
            {WEEKDAYS.map((day) => (
                <label className="flex items-center gap-2 text-sm" key={day}>
                    <input defaultChecked={selected.includes(day)} name="weekdays" type="checkbox" value={day} />
                    {formatWeekday(day)}
                </label>
            ))}
        </div>
    );
}
