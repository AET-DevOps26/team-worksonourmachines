import { getUser } from '../../.server/service/user';

export function BadComponent() {
    return <div>{getUser()}</div>;
}
