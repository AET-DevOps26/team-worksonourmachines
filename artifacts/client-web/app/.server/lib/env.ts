import { type } from 'arktype';

const Env = type({
    LOG_FORMAT: "'pretty' | 'json'",
    LOG_LEVEL: "'debug' | 'info' | 'warn' | 'error'",
    NODE_ENV: "'development' | 'production'",
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
};
