// biome-ignore-all lint/style/noProcessEnv: test fixtures mutate process.env

export const validEnv: Record<string, string> = {
    APP_BASE_URL: 'http://localhost:5173',
    KEYCLOAK_CLIENT_ID: 'client-web',
    KEYCLOAK_CLIENT_SECRET: 'test-secret',
    KEYCLOAK_ISSUER: 'http://localhost:8080/realms/tutormatch',
    LOG_FORMAT: 'json',
    LOG_LEVEL: 'error',
    NODE_ENV: 'development',
    REDIS_URL: 'redis://localhost:6379',
    SERVER_COMMUNICATION_API_URL: 'http://localhost:8081',
    SERVER_MARKETPLACE_API_URL: 'http://localhost:8082',
    SERVER_STUDENT_API_URL: 'http://localhost:8083',
};

export function applyValidEnv(overrides: Record<string, string> = {}): void {
    for (const [key, value] of Object.entries({ ...validEnv, ...overrides })) {
        process.env[key] = value;
    }
}

export function sampleSession(
    overrides: {
        accessToken?: string;
        expiresAt?: number;
        idToken?: string;
        refreshToken?: string;
        roles?: readonly string[];
        sub?: string;
    } = {},
) {
    return {
        accessToken: overrides.accessToken ?? 'access-token',
        expiresAt: overrides.expiresAt ?? Date.now() + 60_000,
        idToken: overrides.idToken,
        refreshToken: overrides.refreshToken,
        user: {
            email: 'user@example.com',
            name: 'Test User',
            preferredUsername: 'testuser',
            roles: overrides.roles ?? ['student'],
            sub: overrides.sub ?? 'user-sub',
        },
    };
}
