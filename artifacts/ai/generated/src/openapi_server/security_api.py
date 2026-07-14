# coding: utf-8

from typing import List

from fastapi import Depends, Security  # noqa: F401
from fastapi.openapi.models import OAuthFlowImplicit, OAuthFlows, OAuthFlowClientCredentials  # noqa: F401
from fastapi.security import (  # noqa: F401
    HTTPAuthorizationCredentials,
    HTTPBasic,
    HTTPBasicCredentials,
    HTTPBearer,
    OAuth2,
    OAuth2AuthorizationCodeBearer,
    OAuth2PasswordBearer,
    SecurityScopes,
)
from fastapi.security.api_key import APIKeyCookie, APIKeyHeader, APIKeyQuery  # noqa: F401

from openapi_server.models.extra_models import TokenModel

oauth2_application = OAuth2(
    flows=OAuthFlows(
        clientCredentials=OAuthFlowClientCredentials(
        tokenUrl="https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/token",
        scopes={
                "openid": "",
                "profile": "",
                "email": "",
                "roles": "",
    },)))


bearer_scheme = HTTPBearer()

# This default is replaced at startup with app.auth.verify_service_token via
# app.include_router(...) after dependency_overrides is set. The fallback here
# accepts any bearer so the generated OpenAPI schema remains valid for Scalar.
def get_token_KeycloakClientAuth(
    security_scopes: SecurityScopes,
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> TokenModel:
    return TokenModel(sub=credentials.credentials)


def validate_scope_KeycloakClientAuth(
    required_scopes: SecurityScopes, token_scopes: List[str]
) -> bool:
    return False
