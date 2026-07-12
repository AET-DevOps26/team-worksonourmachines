import os

import httpx
from fastapi import HTTPException

KEYCLOAK_TOKEN_URL = os.getenv(
    "KEYCLOAK_TOKEN_URL",
    "https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/token",
)
AI_CLIENT_ID = os.getenv("AI_CLIENT_ID", "server-ai")
AI_CLIENT_SECRET = os.getenv("AI_CLIENT_SECRET", "")


async def exchange_token(authorization: str) -> str:
    """Exchange a student JWT for one issued to the AI client, preserving sub."""
    raw_token = authorization.removeprefix("Bearer ").strip()
    async with httpx.AsyncClient(verify=False) as client:
        response = await client.post(
            KEYCLOAK_TOKEN_URL,
            data={
                "grant_type": "urn:ietf:params:oauth:grant-type:token-exchange",
                "client_id": AI_CLIENT_ID,
                "client_secret": AI_CLIENT_SECRET,
                "subject_token": raw_token,
                "subject_token_type": "urn:ietf:params:oauth:token-type:access_token",
                "requested_token_type": "urn:ietf:params:oauth:token-type:access_token",
            },
        )
    if not response.is_success:
        raise HTTPException(
            status_code=502,
            detail=f"Token exchange failed: {response.text}",
        )
    return f"Bearer {response.json()['access_token']}"
