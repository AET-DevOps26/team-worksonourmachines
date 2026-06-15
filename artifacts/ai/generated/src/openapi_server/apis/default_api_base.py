# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.chat_request import ChatRequest
from openapi_server.models.test200_response import Test200Response


class BaseDefaultApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseDefaultApi.subclasses = BaseDefaultApi.subclasses + (cls,)

    async def test(self) -> Test200Response:
        ...

    async def chat(
        self,
        chat_request: ChatRequest,
    ) -> Chat200Response:
        ...
