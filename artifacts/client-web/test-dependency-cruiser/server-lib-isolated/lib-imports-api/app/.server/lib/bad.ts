import { fetchUser } from '../api/user';

export function loadUser() {
    return fetchUser();
}
