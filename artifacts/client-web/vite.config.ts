import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { reactRouter } from '@react-router/dev/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig, type Plugin } from 'vite';
import tsconfigPaths from 'vite-tsconfig-paths';

const root = path.dirname(fileURLToPath(import.meta.url));

// fail startup on keycloak discovery failure
function oidcWarmup(): Plugin {
    return {
        configureServer(server) {
            server.httpServer?.once('listening', () => {
                const authModulePath = path.join(root, 'app/.server/service/auth.ts');
                void server.ssrLoadModule(authModulePath).catch(() => process.exit(1));
            });
        },
        name: 'oidc-warmup',
    };
}

// biome-ignore lint/style/noProcessEnv: Vite config runs outside the app env module
const appBaseUrl = process.env.APP_BASE_URL ?? 'https://tutormatch.localhost';
const appUsesGateway = appBaseUrl.startsWith('https://');
const gatewayHostname = new URL(appBaseUrl).hostname;

export default defineConfig({
    plugins: [tailwindcss(), reactRouter(), tsconfigPaths(), oidcWarmup()],
    build: {
        target: 'esnext',
    },
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
