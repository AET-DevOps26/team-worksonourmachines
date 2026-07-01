type AdminTokenResponse = {
  access_token: string;
  expires_in: number;
};

let cachedToken: { token: string; expiresAt: number } | null = null;

function requireEnv(name: string): string {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

async function getAdminAccessToken(): Promise<string> {
  if (cachedToken && cachedToken.expiresAt > Date.now() + 30_000) {
    return cachedToken.token;
  }

  const issuer = requireEnv('KEYCLOAK_ISSUER');
  const tokenUrl = `${issuer}/protocol/openid-connect/token`;
  const body = new URLSearchParams({
    client_id: requireEnv('KEYCLOAK_ADMIN_CLIENT_ID'),
    client_secret: requireEnv('KEYCLOAK_ADMIN_CLIENT_SECRET'),
    grant_type: 'client_credentials',
  });

  const response = await fetch(tokenUrl, {
    body,
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    method: 'POST',
  });
  if (!response.ok) {
    throw new Error(`Keycloak token request failed: ${response.status}`);
  }

  const data = (await response.json()) as AdminTokenResponse;
  cachedToken = {
    expiresAt: Date.now() + data.expires_in * 1000,
    token: data.access_token,
  };
  return data.access_token;
}

async function adminFetch(path: string, init?: RequestInit): Promise<Response> {
  const token = await getAdminAccessToken();
  const issuer = requireEnv('KEYCLOAK_ISSUER');
  const base = issuer.replace(/\/realms\/[^/]+$/, '');
  const realm = issuer.split('/realms/')[1] ?? 'tutormatch';

  return fetch(`${base}/admin/realms/${realm}${path}`, {
    ...init,
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  });
}

export async function assignTutorRole(userId: string): Promise<void> {
  const rolesResponse = await adminFetch('/roles/tutor');
  if (!rolesResponse.ok) {
    throw new Error(`Failed to fetch tutor role: ${rolesResponse.status}`);
  }
  const tutorRole = await rolesResponse.json();

  const existingResponse = await adminFetch(`/users/${userId}/role-mappings/realm`);
  if (existingResponse.ok) {
    const existing = (await existingResponse.json()) as { name: string }[];
    if (existing.some((r) => r.name === 'tutor')) {
      return;
    }
  }

  const assignResponse = await adminFetch(`/users/${userId}/role-mappings/realm`, {
    body: JSON.stringify([tutorRole]),
    method: 'POST',
  });
  if (!assignResponse.ok) {
    const text = await assignResponse.text();
    throw new Error(`Failed to assign tutor role (${assignResponse.status}): ${text}`);
  }
}
