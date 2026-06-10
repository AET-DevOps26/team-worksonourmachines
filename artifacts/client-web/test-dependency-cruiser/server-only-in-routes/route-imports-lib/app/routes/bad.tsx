import { env } from '../../.server/lib/env';

export async function loader() {
    return env;
}
