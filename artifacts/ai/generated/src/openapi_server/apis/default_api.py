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
from openapi_server.models.test200_response import Test200Response


router = APIRouter()

ns_pkg = openapi_server.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.get(
    "/v1/test",
    responses={
        200: {"model": Test200Response, "description": "The request has succeeded."},
    },
    tags=["default"],
    response_model_by_alias=True,
)
async def test(
) -> Test200Response:
    if not BaseDefaultApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseDefaultApi.subclasses[0]().test()


@router.post(
    "/v1/chat",
    responses={
        200: {"model": Chat200Response, "description": "The request has succeeded."},
    },
    tags=["default"],
    response_model_by_alias=True,
)
async def chat(body: dict = Body(...)) -> Chat200Response:
    if not BaseDefaultApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseDefaultApi.subclasses[0]().chat(prompt=body.get("prompt", ""))
