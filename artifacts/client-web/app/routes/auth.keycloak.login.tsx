import type { LoaderFunctionArgs } from 'react-router';
import { startKeycloakLogin } from '~/.server/service/keycloak';

export async function loader({ request }: LoaderFunctionArgs) {
    return startKeycloakLogin(request);
}
