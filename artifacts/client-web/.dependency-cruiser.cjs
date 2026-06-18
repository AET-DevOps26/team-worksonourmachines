/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
    forbidden: [
        {
            name: 'server-only-imported-by-routes',
            severity: 'error',
            comment: 'Only routes may import from .server/',
            from: { pathNot: ['(^|/)routes/', '(^|/)\\.server/'] },
            to: { path: '(^|/)\\.server/' },
        },
        {
            name: 'routes-only-use-server-service',
            severity: 'error',
            comment: 'Routes must use .server/service/, not .server/api/ or .server/lib/',
            from: { path: '(^|/)routes/' },
            to: { path: '(^|/)\\.server/', pathNot: '(^|/)\\.server/service/' },
        },
        {
            name: 'self-contained-server',
            severity: 'error',
            comment: '.server/ code must not import from routes/, components/, or client lib/',
            from: { path: '(^|/)\\.server/' },
            to: { path: '(^|/)(routes/|components/|(?<!\\.server/)lib/)' },
        },
        {
            name: 'server-api-no-service',
            severity: 'error',
            comment: '.server/api/ must not import from .server/service/',
            from: { path: '(^|/)\\.server/api/' },
            to: { path: '(^|/)\\.server/service/' },
        },
        {
            name: 'server-lib-isolated',
            severity: 'error',
            comment: '.server/lib/ must not import from .server/api/ or .server/service/',
            from: { path: '(^|/)\\.server/lib/' },
            to: { path: '(^|/)\\.server/(api|service)/' },
        },
        {
            name: 'dumb-components',
            severity: 'error',
            comment:
                'components/ui/ and components/<feature>/ui/ must not import from app code (sibling ui/ imports are allowed)',
            from: { path: '((?:^|/)components/(?:[^/]+/ui/|ui/))[^/]+' },
            to: {
                path: '(^|/)(routes/|lib/|\\.server/|components/)',
                pathNot: ['$1', '(^|/)lib/ui/'],
            },
        },
        {
            name: 'feature-index',
            severity: 'error',
            comment: 'Import feature modules only through their index.ts or index.tsx',
            from: { pathNot: '(^|/)components/(?!ui(?:/|$))[^/]+/' },
            to: {
                path: '(^|/)components/(?!ui(?:/|$))[^/]+/',
                pathNot: '(^|/)components/(?!ui(?:/|$))[^/]+/index\\.tsx?$',
            },
        },
        {
            name: 'feature-index',
            severity: 'error',
            comment: 'Cross-feature imports must go through the target feature index.ts or index.tsx',
            from: { path: '(^|/)components/(?!ui(?:/|$))([^/]+)/' },
            to: {
                path: '(^|/)components/(?!ui(?:/|$))([^/]+)/',
                pathNot: ['(^|/)components/$2/', '(^|/)components/[^/]+/index\\.tsx?$'],
            },
        },
        {
            name: 'client-lib-isolated',
            severity: 'error',
            comment: 'Client lib/ must not import from routes/ or components/',
            from: { path: '(^|/)(?<!\\.server/)lib/' },
            to: { path: '(^|/)(routes/|components/)' },
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
