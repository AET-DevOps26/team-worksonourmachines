import type { LoaderFunctionArgs } from 'react-router';
import { startKeycloakLogin } from '~/.server/keycloak';

export async function loader({ request }: LoaderFunctionArgs) {
    return startKeycloakLogin(request);
}
