# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from openapi_server.models.generate_plan_request import GeneratePlanRequest
from openapi_server.models.generate_plan_response import GeneratePlanResponse


class BaseDefaultApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseDefaultApi.subclasses = BaseDefaultApi.subclasses + (cls,)

    async def generate_plan(
        self,
        body: GeneratePlanRequest,
        authorization: str,
    ) -> GeneratePlanResponse:
        """Generate three study-plan suggestions for a learning goal."""
        ...

