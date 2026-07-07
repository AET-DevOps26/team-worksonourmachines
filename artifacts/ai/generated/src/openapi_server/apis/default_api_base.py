# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.chat_request import ChatRequest
from openapi_server.models.generate_plan_request import GeneratePlanRequest
from openapi_server.models.generate_plan_response import GeneratePlanResponse


class BaseDefaultApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseDefaultApi.subclasses = BaseDefaultApi.subclasses + (cls,)

    async def chat(
        self,
        chat_request: ChatRequest,
    ) -> Chat200Response:
        """Sends a prompt to the AI service and returns a generated response."""
        ...

    async def generate_plan(
        self,
        body: GeneratePlanRequest,
        authorization: str,
    ) -> GeneratePlanResponse:
        """Generate three study-plan suggestions for a learning goal."""
        ...

