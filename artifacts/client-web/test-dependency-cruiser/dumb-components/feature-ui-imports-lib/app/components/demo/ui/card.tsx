import { formatName } from '../../../../lib/format';

export function Card({ name }: { name: string }) {
    return <div>{formatName(name)}</div>;
}
