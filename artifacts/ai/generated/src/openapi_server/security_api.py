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


def get_token_KeycloakClientAuth(
    security_scopes: SecurityScopes,
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> TokenModel:
    return TokenModel(sub=credentials.credentials)


def validate_scope_KeycloakClientAuth(
    required_scopes: SecurityScopes, token_scopes: List[str]
) -> bool:
    """
    Validate required scopes are included in token scope

    :param required_scopes Required scope to access called API
    :type required_scopes: List[str]
    :param token_scopes Scope present in token
    :type token_scopes: List[str]
    :return: True if access to allowed API is allowed
    :rtype: bool
    """

    return False
