/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
    forbidden: [
        {
            name: 'no-server-in-ui',
            severity: 'error',
            comment: 'Client code must not import server modules',
            from: { path: '^app/(components|lib)/' },
            to: { path: '^app/\\.server/' },
        },
        {
            name: 'no-ui-in-server',
            severity: 'error',
            comment: 'Server code must not import UI components',
            from: { path: '^app/\\.server/' },
            to: { path: '^app/components/' },
        },
        {
            name: 'routes-via-service',
            severity: 'error',
            comment: 'Routes call services, not raw API clients',
            from: { path: '^app/routes/' },
            to: { path: '^app/\\.server/api/' },
        },
        {
            name: 'api-not-service',
            severity: 'error',
            comment: 'API layer must not depend on services',
            from: { path: '^app/\\.server/api/' },
            to: { path: '^app/\\.server/service/' },
        },
        {
            name: 'feature-barrel-only',
            severity: 'error',
            comment: 'Import features only through their index file',
            from: { pathNot: '^app/components/[^/]+/' },
            to: {
                path: '^app/components/[^/]+/',
                pathNot: ['^app/components/[^/]+/index\\.tsx?$', '^app/components/[^/]+/ui/'],
            },
        },
        {
            name: 'feature-ui-private',
            severity: 'error',
            comment: 'Feature ui/ is private to that feature',
            from: { pathNot: '^app/components/([^/]+)/' },
            to: { path: '^app/components/$1/ui/' },
        },
    ],
    options: {
        doNotFollow: {
            path: 'node_modules',
        },
        tsPreCompilationDeps: true,
        tsConfig: {
            fileName: 'tsconfig.json',
        },
        exclude: {
            path: '(^node_modules)|(/generated/)',
        },
    },
};
