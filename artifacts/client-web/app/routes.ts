import { index, layout, prefix, type RouteConfig, route } from '@react-router/dev/routes';

export default [
    layout('routes/app-layout.tsx', [
        index('routes/home.tsx'),
        route('about', 'routes/about.tsx'),
        route('become-a-tutor', 'routes/become-a-tutor.tsx'),
        route('terms', 'routes/terms.tsx'),
        route('privacy', 'routes/privacy.tsx'),
        route('discover', 'routes/discover.tsx'),
        route('notifications', 'routes/notifications.tsx'),
        ...prefix('modules', [index('routes/modules/index.tsx'), route(':code', 'routes/modules/$code.tsx')]),
        ...prefix('tutors', [route(':id', 'routes/tutors/$id.tsx')]),
        ...prefix('plans', [route(':id', 'routes/plans/$id.tsx')]),
        ...prefix('me', [
            route('profile', 'routes/me/profile.tsx'),
            ...prefix('goals', [
                index('routes/me/goals/index.tsx'),
                route('new', 'routes/me/goals/new.tsx'),
                route(':id', 'routes/me/goals/$id.tsx'),
            ]),
        ]),
        ...prefix('tutor', [
            route('apply', 'routes/tutor/apply.tsx'),
            route('profile', 'routes/tutor/profile.tsx'),
            route('dashboard', 'routes/tutor/dashboard.tsx'),
        ]),
        ...prefix('chat', [index('routes/chat/index.tsx'), route(':id', 'routes/chat/$id.tsx')]),
        ...prefix('admin', [
            route('tutor-approvals', 'routes/admin/tutor-approvals.tsx'),
            ...prefix('modules', [
                index('routes/admin/modules.tsx'),
                route('new', 'routes/admin/modules/new.tsx'),
                route(':code', 'routes/admin/modules/$code.tsx'),
            ]),
        ]),
    ]),
    route('login', 'routes/login.tsx'),
    ...prefix('auth', [
        route('login', 'routes/auth/login.tsx'),
        route('register', 'routes/auth/register.tsx'),
        route('callback', 'routes/auth/callback.tsx'),
        route('logout', 'routes/auth/logout.tsx'),
    ]),
] satisfies RouteConfig;
