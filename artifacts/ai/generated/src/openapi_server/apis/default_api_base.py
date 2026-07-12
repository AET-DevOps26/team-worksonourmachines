# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from openapi_server.models.shared_ai_generate_plan_request import SharedAiGeneratePlanRequest
from openapi_server.models.shared_ai_generate_plan_response import SharedAiGeneratePlanResponse
from openapi_server.models.shared_errors_error_body import SharedErrorsErrorBody
from openapi_server.security_api import get_token_KeycloakClientAuth

class BaseDefaultApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseDefaultApi.subclasses = BaseDefaultApi.subclasses + (cls,)
    async def generate_plan(
        self,
        shared_ai_generate_plan_request: SharedAiGeneratePlanRequest,
    ) -> SharedAiGeneratePlanResponse:
        """Fetches the learning goal and student from the Student API, tutors from the Marketplace API, then calls the LLM to generate three study-plan suggestions (cheapest, within_budget, best_quality). Does not persist anything."""
        ...
