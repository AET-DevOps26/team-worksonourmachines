import { fetchUser } from '../../.server/api/user';

export async function loader() {
    return fetchUser();
}
