import os

import httpx
from fastapi import HTTPException

KEYCLOAK_TOKEN_URL = os.getenv(
    "KEYCLOAK_TOKEN_URL",
    "http://keycloak:8080/realms/tutormatch/protocol/openid-connect/token",
)
AI_CLIENT_ID = os.getenv("AI_CLIENT_ID", "server-ai")
AI_CLIENT_SECRET = os.getenv("AI_CLIENT_SECRET", "")


async def get_service_token() -> str:
    """Obtain a service token for the AI client using client credentials grant."""
    async with httpx.AsyncClient(verify=False) as client:
        response = await client.post(
            KEYCLOAK_TOKEN_URL,
            data={
                "grant_type": "client_credentials",
                "client_id": AI_CLIENT_ID,
                "client_secret": AI_CLIENT_SECRET,
            },
        )
    if not response.is_success:
        raise HTTPException(
            status_code=502,
            detail=f"Failed to obtain service token: {response.text}",
        )
    return f"Bearer {response.json()['access_token']}"
