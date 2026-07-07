import type { SharedMarketplaceTutorAvailability } from '~/.server/api/server-marketplace/generated';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';

const WEEKDAYS = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'] as const;

function formatWeekday(day: (typeof WEEKDAYS)[number]) {
    return day.charAt(0).toUpperCase() + day.slice(1);
}

export function parseAvailabilityFromFormData(formData: FormData): SharedMarketplaceTutorAvailability[] {
    return WEEKDAYS.map((weekday) => {
        const note = String(formData.get(`availability_${weekday}_note`) ?? '').trim();
        const entry: SharedMarketplaceTutorAvailability = {
            available: formData.get(`availability_${weekday}_enabled`) === 'on',
            weekday,
            ...(note ? { note } : {}),
        };
        return entry;
    }).filter((entry) => entry.available);
}

type TutorAvailabilityFieldsProps = {
    availability?: readonly SharedMarketplaceTutorAvailability[];
};

export function TutorAvailabilityFields({ availability }: TutorAvailabilityFieldsProps) {
    return (
        <div className="flex flex-col gap-3">
            {WEEKDAYS.map((day) => {
                const existing = availability?.find((entry) => entry.weekday === day);
                const enabled = existing?.available ?? false;

                return (
                    <div className="flex flex-col gap-2 rounded-lg border border-border p-3" key={day}>
                        <label className="flex items-center gap-2 text-sm font-medium">
                            <input defaultChecked={enabled} name={`availability_${day}_enabled`} type="checkbox" />
                            {formatWeekday(day)}
                        </label>
                        <div className="flex flex-col gap-1">
                            <Label className="text-xs text-muted-foreground" htmlFor={`availability_${day}_note`}>
                                Additional details (optional)
                            </Label>
                            <Input
                                defaultValue={existing?.note ?? ''}
                                id={`availability_${day}_note`}
                                name={`availability_${day}_note`}
                                placeholder="e.g. 18:00-21:00 or flexible afternoons"
                            />
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

type TutorAvailabilityDisplayProps = {
    availability: readonly SharedMarketplaceTutorAvailability[];
};

export function TutorAvailabilityDisplay({ availability }: TutorAvailabilityDisplayProps) {
    const availableDays = availability.filter((entry) => entry.available);

    if (availableDays.length === 0) {
        return <p className="text-sm text-muted-foreground">No availability listed.</p>;
    }

    return (
        <div className="flex flex-col gap-2">
            {availableDays.map((entry) => (
                <div className="flex justify-between gap-4 text-sm" key={entry.weekday}>
                    <span className="font-medium capitalize">{entry.weekday}</span>
                    <span className="text-right text-muted-foreground">{entry.note ?? 'Available'}</span>
                </div>
            ))}
        </div>
    );
}
