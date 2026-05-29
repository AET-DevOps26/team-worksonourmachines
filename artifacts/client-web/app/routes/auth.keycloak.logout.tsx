import type { LoaderFunctionArgs } from 'react-router';
import { endKeycloakSession } from '~/.server/keycloak';

export async function loader({ request }: LoaderFunctionArgs) {
    return endKeycloakSession(request);
}
