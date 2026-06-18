import { index, layout, type RouteConfig, route } from '@react-router/dev/routes';

export default [
    layout('routes/app-layout.tsx', [index('routes/home.tsx')]),
    route('auth/keycloak/login', 'routes/auth.keycloak.login.tsx'),
    route('auth/keycloak/callback', 'routes/auth.keycloak.callback.tsx'),
    route('auth/keycloak/logout', 'routes/auth.keycloak.logout.tsx'),
] satisfies RouteConfig;
