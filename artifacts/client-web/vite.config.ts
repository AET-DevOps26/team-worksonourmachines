import { reactRouter } from '@react-router/dev/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vite';
import tsconfigPaths from 'vite-tsconfig-paths';

const appBaseUrl = process.env.APP_BASE_URL ?? 'https://tutormatch.localhost';
const appUsesGateway = appBaseUrl.startsWith('https://');
const gatewayHostname = new URL(appBaseUrl).hostname;

export default defineConfig({
    plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
    server: {
        allowedHosts: [gatewayHostname, 'localhost'],
        hmr: appUsesGateway
            ? {
                  clientPort: 443,
                  host: gatewayHostname,
                  path: '/ws',
                  protocol: 'wss',
              }
            : {
                  clientPort: 5173,
                  path: '/ws',
                  port: 5173,
              },
        host: '0.0.0.0',
        port: 5173,
        strictPort: true,
    },
});
