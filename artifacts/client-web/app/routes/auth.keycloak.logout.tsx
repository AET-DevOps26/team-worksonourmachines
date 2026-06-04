import type { LoaderFunctionArgs } from 'react-router';
import { endKeycloakSession } from '~/.server/service/keycloak';

export async function loader({ request }: LoaderFunctionArgs) {
    return endKeycloakSession(request);
}
