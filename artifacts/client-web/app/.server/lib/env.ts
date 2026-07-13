// biome-ignore-all lint/style/noProcessEnv: centralized env access

import { type } from 'arktype';

const Env = type({
    APP_BASE_URL: 'string',
    KEYCLOAK_CLIENT_ID: 'string',
    KEYCLOAK_CLIENT_SECRET: 'string',
    KEYCLOAK_ISSUER: 'string',
    LOG_FORMAT: "'pretty' | 'json'",
    LOG_LEVEL: "'debug' | 'info' | 'warn' | 'error'",
    NODE_ENV: "'development' | 'production'",
    REDIS_URL: 'string',
    SERVER_AI_API_URL: 'string',
    SERVER_COMMUNICATION_API_URL: 'string',
    SERVER_MARKETPLACE_API_URL: 'string',
    SERVER_STUDENT_API_URL: 'string',
});

type Env = typeof Env.infer;

const checkedEnv = Env.assert(process.env);

function get<K extends keyof Env>(key: K): Env[K] {
    return checkedEnv[key];
}

export const env = {
    get,
    isDev: get('NODE_ENV') === 'development',
    isSecureCookies: get('APP_BASE_URL').startsWith('https://'),
};
