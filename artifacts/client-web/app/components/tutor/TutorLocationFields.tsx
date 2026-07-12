import type { SharedMarketplaceLocation } from '~/.server/api/server-marketplace/generated';

const LOCATION_OPTIONS: { value: SharedMarketplaceLocation; label: string }[] = [
    { label: 'Online', value: 'online' },
    { label: 'Garching', value: 'garching' },
    { label: 'Munich', value: 'munich' },
    { label: 'Weihenstephan', value: 'weihenstephan' },
    { label: 'Straubing', value: 'straubing' },
    { label: 'Ottobrunn', value: 'ottobrunn' },
];

export function formatLocationLabel(location: SharedMarketplaceLocation): string {
    return LOCATION_OPTIONS.find((option) => option.value === location)?.label ?? location;
}

export function formatLocations(locations: readonly SharedMarketplaceLocation[]): string {
    return locations.map(formatLocationLabel).join(', ');
}

export function parseLocationsFromFormData(formData: FormData): SharedMarketplaceLocation[] {
    return LOCATION_OPTIONS.map((option) => option.value).filter(
        (location) => formData.get(`location_${location}`) === 'on',
    );
}

type TutorLocationFieldsProps = {
    locations?: readonly SharedMarketplaceLocation[];
};

export function TutorLocationFields({ locations }: TutorLocationFieldsProps) {
    return (
        <div className="flex flex-wrap gap-3">
            {LOCATION_OPTIONS.map((option) => (
                <label className="flex items-center gap-2 text-sm" key={option.value}>
                    <input
                        defaultChecked={locations?.includes(option.value) ?? option.value === 'online'}
                        name={`location_${option.value}`}
                        type="checkbox"
                    />
                    {option.label}
                </label>
            ))}
        </div>
    );
}

export function TutorLocationFilterOptions() {
    return LOCATION_OPTIONS.map((option) => (
        <option key={option.value} value={option.value}>
            {option.label}
        </option>
    ));
}
