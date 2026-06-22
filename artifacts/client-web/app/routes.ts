import { index, prefix, type RouteConfig, route } from '@react-router/dev/routes';

export default [
    index('routes/home.tsx'),
    route('login', 'routes/login.tsx'),
    ...prefix('auth', [
        route('login', 'routes/auth/login.tsx'),
        route('callback', 'routes/auth/callback.tsx'),
        route('logout', 'routes/auth/logout.tsx'),
    ]),
] satisfies RouteConfig;
