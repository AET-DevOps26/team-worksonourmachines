import type { LoaderFunctionArgs } from 'react-router';
import { completeKeycloakLogin } from '~/.server/service/keycloak';

export async function loader({ request }: LoaderFunctionArgs) {
    return completeKeycloakLogin(request);
}
