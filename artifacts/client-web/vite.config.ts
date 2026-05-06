import { reactRouter } from '@react-router/dev/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vite';
import tsconfigPaths from 'vite-tsconfig-paths';

export default defineConfig({
    plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
    server: {
        allowedHosts: true,
        hmr: {
            // todo: adjust vite clientPort after configuring reverse proxy
            clientPort: 5173,
            path: '/ws',
            port: 5173,
        },
        host: '0.0.0.0',
        port: 5173,
        strictPort: true,
    },
});
