# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from openapi_server.apis.default_api_base import BaseDefaultApi
import openapi_server.impl

from fastapi import (  # noqa: F401
    APIRouter,
    Body,
    Cookie,
    Depends,
    Form,
    Header,
    HTTPException,
    Path,
    Query,
    Response,
    Security,
    status,
)

from openapi_server.models.extra_models import TokenModel  # noqa: F401
from openapi_server.models.shared_ai_generate_plan_request import SharedAiGeneratePlanRequest
from openapi_server.models.shared_ai_generate_plan_response import SharedAiGeneratePlanResponse
from openapi_server.models.shared_errors_error_body import SharedErrorsErrorBody
from openapi_server.security_api import get_token_KeycloakAuth

router = APIRouter()

ns_pkg = openapi_server.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/v1/plan",
    responses={
        200: {"model": SharedAiGeneratePlanResponse, "description": "The request has succeeded."},
        401: {"model": SharedErrorsErrorBody, "description": "Access is unauthorized."},
        404: {"model": SharedErrorsErrorBody, "description": "The server cannot find the requested resource."},
    },
    tags=["default"],
    summary="Generate study plan",
    response_model_by_alias=True,
)
async def generate_plan(
    shared_ai_generate_plan_request: SharedAiGeneratePlanRequest = Body(None, description=""),
    token_KeycloakAuth: TokenModel = Security(
        get_token_KeycloakAuth, scopes=["openid", "basic", "profile", "email", "roles"]
    ),
) -> SharedAiGeneratePlanResponse:
    """Fetches the learning goal and student from the Student API, tutors from the Marketplace API, then calls the LLM to generate three study-plan suggestions (cheapest, within_budget, best_quality). Does not persist anything."""
    if not BaseDefaultApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseDefaultApi.subclasses[0]().generate_plan(shared_ai_generate_plan_request)
