import path from 'node:path';
import { fileURLToPath } from 'node:url';
import tsconfigPaths from 'vite-tsconfig-paths';
import { defineConfig } from 'vitest/config';

const root = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
    plugins: [tsconfigPaths()],
    resolve: {
        alias: {
            '~': path.join(root, 'app'),
        },
    },
    root,
    test: {
        environment: 'node',
        include: ['app/**/*.test.ts'],
        setupFiles: ['app/.server/test/setup.ts'],
    },
});
