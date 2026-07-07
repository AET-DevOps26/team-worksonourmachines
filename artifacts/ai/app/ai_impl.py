import json
import logging
import time

from langchain_core.messages import HumanMessage
from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.chat_request import ChatRequest
from openapi_server.models.generate_plan_request import GeneratePlanRequest
from openapi_server.models.generate_plan_response import (
    GeneratePlanResponse,
    PlanMilestone,
    PlanSuggestion,
    ProposedTutor,
)

from app.clients import get_learning_goal, get_module, get_student_profile, list_tutors
from app.llm import get_llm, get_llm_info
from app.prompt import build_prompt

logger = logging.getLogger(__name__)


def _map_response(data: dict) -> GeneratePlanResponse:
    suggestions = []
    for s in data.get("suggestions", []):
        tutors = [ProposedTutor.from_dict(t) for t in s.get("proposedTutors", [])]
        milestones = [PlanMilestone.from_dict(m) for m in s.get("milestones", [])]
        suggestions.append(
            PlanSuggestion(
                tier=s["tier"],
                description=s.get("description", ""),
                totalEstimatedCost=s.get("totalEstimatedCost", 0.0),
                proposedTutors=tutors,
                milestones=milestones,
            )
        )
    return GeneratePlanResponse(
        learningGoalId=data["learningGoalId"],
        suggestions=suggestions,
    )


class DefaultApiImpl(BaseDefaultApi):
    async def chat(self, chat_request: ChatRequest) -> Chat200Response:
        prompt_length = len(chat_request.prompt)
        llm_info = get_llm_info()
        start = time.perf_counter()
        try:
            response = await get_llm().ainvoke(
                [HumanMessage(content=chat_request.prompt)]
            )
            latency_ms = round((time.perf_counter() - start) * 1000, 1)
            logger.info(
                "chat completed provider=%s model=%s latency_ms=%s"
                " prompt_length=%s response_length=%s",
                llm_info["provider"],
                llm_info["model"],
                latency_ms,
                prompt_length,
                len(response.content),
            )
            return Chat200Response(message=response.content)
        except Exception:
            latency_ms = round((time.perf_counter() - start) * 1000, 1)
            logger.exception(
                "chat failed provider=%s model=%s latency_ms=%s prompt_length=%s",
                llm_info["provider"],
                llm_info["model"],
                latency_ms,
                prompt_length,
            )
            raise

    async def generate_plan(
        self, body: GeneratePlanRequest, authorization: str
    ) -> GeneratePlanResponse:
        goal_id = body.learning_goal_id
        llm_info = get_llm_info()
        start = time.perf_counter()

        goal = await get_learning_goal(goal_id, authorization)
        student = await get_student_profile(authorization)
        module = await get_module(goal["moduleId"], authorization)
        tutors = await list_tutors(
            module_id=goal["moduleId"],
            languages=student.get("languages", []),
            locations=goal.get("locations", []),
            authorization=authorization,
        )

        prompt = build_prompt(student, goal, module, tutors)

        try:
            raw = await get_llm().ainvoke([HumanMessage(content=prompt)])
            latency_ms = round((time.perf_counter() - start) * 1000, 1)
            logger.info(
                "generate_plan completed provider=%s model=%s latency_ms=%s goal_id=%s",
                llm_info["provider"],
                llm_info["model"],
                latency_ms,
                goal_id,
            )
        except Exception:
            latency_ms = round((time.perf_counter() - start) * 1000, 1)
            logger.exception(
                "generate_plan llm failed provider=%s model=%s"
                " latency_ms=%s goal_id=%s",
                llm_info["provider"],
                llm_info["model"],
                latency_ms,
                goal_id,
            )
            raise

        try:
            data = json.loads(raw.content)
        except json.JSONDecodeError:
            logger.error(
                "generate_plan json parse failed goal_id=%s response=%s",
                goal_id,
                raw.content[:500],
            )
            from fastapi import HTTPException

            raise HTTPException(status_code=500, detail="LLM returned non-JSON output")

        return _map_response(data)
