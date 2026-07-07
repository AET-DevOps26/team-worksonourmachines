import json
import logging
import re
import time

from fastapi import HTTPException
from langchain_core.messages import HumanMessage
from openapi_server.apis.default_api_base import BaseDefaultApi
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

_FENCE_RE = re.compile(r"```(?:json)?\s*([\s\S]*?)```", re.IGNORECASE)


def _extract_json(text: str) -> dict:
    """Strip markdown fences and parse JSON. Raises ValueError on failure."""
    stripped = text.strip()
    match = _FENCE_RE.search(stripped)
    candidate = match.group(1).strip() if match else stripped
    return json.loads(candidate)


def _map_response(data: dict) -> GeneratePlanResponse:
    suggestions = []
    for s in data.get("suggestions", []):
        tutors = [ProposedTutor.from_dict(t) for t in s.get("proposedTutors", [])]
        milestones = [PlanMilestone.from_dict(m) for m in s.get("milestones", [])]
        suggestions.append(
            PlanSuggestion(
                tier=s["tier"],
                description=s.get("description", ""),
                totalEstimatedCost=s.get("totalEstimatedCost", 0),
                proposedTutors=tutors,
                milestones=milestones,
            )
        )
    return GeneratePlanResponse(
        learningGoalId=data["learningGoalId"],
        suggestions=suggestions,
    )


class DefaultApiImpl(BaseDefaultApi):
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

        # Prefer structured output (function calling) when the provider supports it —
        # OpenAI and Logos enforce the schema at the API level so the response is always
        # a valid GeneratePlanResponse with no parsing needed. Ollama and LM Studio may
        # not support tool use depending on the loaded model, so we fall back to plain
        # text generation + fence stripping + one retry for those cases.
        llm = get_llm()
        if get_llm_info()["provider"] in ("openai", "logos"):
            structured = llm.with_structured_output(GeneratePlanResponse)
            result: GeneratePlanResponse = await structured.ainvoke(
                [HumanMessage(content=prompt)]
            )
            latency_ms = round((time.perf_counter() - start) * 1000, 1)
            logger.info(
                "generate_plan completed (structured) provider=%s model=%s"
                " latency_ms=%s goal_id=%s",
                get_llm_info()["provider"],
                get_llm_info()["model"],
                latency_ms,
                goal_id,
            )
            return result

        raw = await self._invoke_llm(prompt, goal_id)

        try:
            data = _extract_json(raw.content)
        except (json.JSONDecodeError, ValueError):
            logger.warning(
                "generate_plan non-json response, retrying goal_id=%s preview=%s",
                goal_id,
                raw.content[:200],
            )
            retry_prompt = (
                prompt + "\n\nIMPORTANT: Your previous response was not valid JSON."
                " Return only the raw JSON object, no markdown, no explanation."
            )
            raw = await self._invoke_llm(retry_prompt, goal_id, attempt=2)
            try:
                data = _extract_json(raw.content)
            except (json.JSONDecodeError, ValueError):
                logger.error(
                    "generate_plan json parse failed after retry goal_id=%s"
                    " response=%s",
                    goal_id,
                    raw.content[:500],
                )
                raise HTTPException(
                    status_code=500, detail="LLM returned non-JSON output"
                )

        latency_ms = round((time.perf_counter() - start) * 1000, 1)
        logger.info(
            "generate_plan completed provider=%s model=%s latency_ms=%s goal_id=%s",
            llm_info["provider"],
            llm_info["model"],
            latency_ms,
            goal_id,
        )
        return _map_response(data)

    async def _invoke_llm(self, prompt: str, goal_id: str, attempt: int = 1):
        llm_info = get_llm_info()
        try:
            return await get_llm().ainvoke([HumanMessage(content=prompt)])
        except Exception:
            logger.exception(
                "generate_plan llm failed provider=%s model=%s goal_id=%s attempt=%s",
                llm_info["provider"],
                llm_info["model"],
                goal_id,
                attempt,
            )
            raise
