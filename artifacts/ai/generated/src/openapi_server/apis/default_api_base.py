# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from openapi_server.models.chat200_response import Chat200Response


class BaseDefaultApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseDefaultApi.subclasses = BaseDefaultApi.subclasses + (cls,)

    async def chat(self, prompt: str) -> Chat200Response:
        ...

