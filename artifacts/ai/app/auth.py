import os
from typing import Annotated

import httpx
import jwt
from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from openapi_server.models.extra_models import TokenModel

# The issuer claim inside tokens uses the public-facing URL Keycloak advertises.
KEYCLOAK_ISSUER = os.getenv(
    "KEYCLOAK_ISSUER",
    "https://auth.tutormatch.localhost/realms/tutormatch",
)
# The JWKS endpoint is fetched service-to-service, so we use the internal URL.
KEYCLOAK_JWKS_URI = os.getenv(
    "KEYCLOAK_JWKS_URI",
    "http://keycloak:8080/realms/tutormatch/protocol/openid-connect/certs",
)
EXPECTED_AZP = os.getenv("EXPECTED_CALLER_CLIENT_ID", "server-student")

_bearer_scheme = HTTPBearer()

_jwks_client: jwt.PyJWKClient | None = None


def _get_jwks_client() -> jwt.PyJWKClient:
    global _jwks_client
    if _jwks_client is None:
        _jwks_client = jwt.PyJWKClient(KEYCLOAK_JWKS_URI, cache_keys=True)
    return _jwks_client


async def verify_service_token(
    credentials: Annotated[HTTPAuthorizationCredentials, Depends(_bearer_scheme)],
) -> TokenModel:
    """Validate the inbound bearer token is a real service token from server-student.

    Returns a TokenModel so it fits the generated route's expected type.
    Raises HTTP 401/403 on any validation failure.
    """
    raw_token = credentials.credentials
    try:
        signing_key = _get_jwks_client().get_signing_key_from_jwt(raw_token)
        payload = jwt.decode(
            raw_token,
            signing_key.key,
            algorithms=["RS256"],
            issuer=KEYCLOAK_ISSUER,
            options={"require": ["exp", "iss", "sub", "azp"]},
        )
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token has expired")
    except jwt.InvalidIssuerError:
        raise HTTPException(status_code=401, detail="Invalid token issuer")
    except (jwt.PyJWKClientError, httpx.RequestError) as exc:
        raise HTTPException(status_code=401, detail=f"JWKS fetch failed: {exc}")
    except jwt.PyJWTError as exc:
        raise HTTPException(status_code=401, detail=f"Invalid token: {exc}")

    azp = payload.get("azp")
    if azp != EXPECTED_AZP:
        raise HTTPException(
            status_code=403,
            detail=(
                f"Token not issued to expected caller"
                f" (got '{azp}', want '{EXPECTED_AZP}')"
            ),
        )

    realm_roles = payload.get("realm_roles") or (
        (payload.get("realm_access") or {}).get("roles") or []
    )
    if "service" not in realm_roles:
        raise HTTPException(
            status_code=403,
            detail="Caller service account is missing the 'service' realm role",
        )

    # Return TokenModel so the generated route's type annotation is satisfied.
    # The token value is stored in .sub but generate_plan ignores authorization entirely
    # — all upstream calls use get_service_token() (AI client credentials).
    return TokenModel(sub=raw_token)
