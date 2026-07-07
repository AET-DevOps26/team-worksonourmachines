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
from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.chat_request import ChatRequest
from openapi_server.models.generate_plan_request import GeneratePlanRequest
from openapi_server.models.generate_plan_response import GeneratePlanResponse


router = APIRouter()

ns_pkg = openapi_server.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/v1/chat",
    responses={
        200: {"model": Chat200Response, "description": "The request has succeeded."},
    },
    tags=["default"],
    summary="Chat with AI",
    response_model_by_alias=True,
)
async def chat(
    chat_request: ChatRequest = Body(None, description=""),
) -> Chat200Response:
    """Sends a prompt to the AI service and returns a generated response."""
    if not BaseDefaultApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseDefaultApi.subclasses[0]().chat(chat_request)


@router.post(
    "/v1/plan",
    responses={
        200: {"model": GeneratePlanResponse, "description": "The request has succeeded."},
        401: {"description": "Access is unauthorized."},
        404: {"description": "The server cannot find the requested resource."},
    },
    tags=["default"],
    summary="Generate study plan",
    response_model_by_alias=True,
)
async def generate_plan(
    body: GeneratePlanRequest = Body(..., description=""),
    authorization: str = Header(..., alias="Authorization"),
) -> GeneratePlanResponse:
    """Generate three study-plan suggestions for a learning goal."""
    if not BaseDefaultApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseDefaultApi.subclasses[0]().generate_plan(body, authorization)

