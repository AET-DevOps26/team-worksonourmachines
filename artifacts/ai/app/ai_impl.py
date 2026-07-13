import asyncio
import json
import logging
import re
import time

from fastapi import HTTPException
from langchain_core.messages import HumanMessage
from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.shared_ai_generate_plan_request import (
    SharedAiGeneratePlanRequest,
)
from openapi_server.models.shared_ai_generate_plan_response import (
    SharedAiGeneratePlanResponse,
)
from openapi_server.models.shared_ai_plan_milestone import SharedAiPlanMilestone
from openapi_server.models.shared_ai_plan_suggestion import SharedAiPlanSuggestion
from openapi_server.models.shared_ai_proposed_tutor import SharedAiProposedTutor
from pydantic import ValidationError

from app.clients import get_learning_goal, get_module, get_student_profile, list_tutors
from app.llm import get_llm, get_llm_info
from app.prompt import build_prompt
from app.token_exchange import exchange_token

logger = logging.getLogger(__name__)

_FENCE_RE = re.compile(r"```(?:json)?\s*([\s\S]*?)```", re.IGNORECASE)


def _extract_json(text: str) -> dict:
    """Strip markdown fences and parse JSON. Raises ValueError on failure."""
    stripped = text.strip()
    match = _FENCE_RE.search(stripped)
    candidate = match.group(1).strip() if match else stripped
    return json.loads(candidate)


def _map_response(data: dict) -> SharedAiGeneratePlanResponse:
    suggestions = []
    for s in data.get("suggestions", []):
        tutors = [
            SharedAiProposedTutor.from_dict(
                {**t, "hourlyRate": int(t["hourlyRate"])} if "hourlyRate" in t else t
            )
            for t in s.get("proposedTutors", [])
        ]
        milestones = [
            SharedAiPlanMilestone.from_dict(
                {**m, "estimatedCost": int(m["estimatedCost"])}
                if "estimatedCost" in m
                else m
            )
            for m in s.get("milestones", [])
        ]
        suggestions.append(
            SharedAiPlanSuggestion(
                tier=s["tier"],
                description=s.get("description", ""),
                totalEstimatedCost=int(s.get("totalEstimatedCost", 0)),
                proposedTutors=tutors,
                milestones=milestones,
            )
        )
    return SharedAiGeneratePlanResponse(
        learningGoalId=data["learningGoalId"],
        suggestions=suggestions,
    )


class DefaultApiImpl(BaseDefaultApi):
    async def generate_plan(
        self,
        shared_ai_generate_plan_request: SharedAiGeneratePlanRequest,
        authorization: str = "",
    ) -> SharedAiGeneratePlanResponse:
        goal_id = shared_ai_generate_plan_request.learning_goal_id
        llm_info = get_llm_info()
        start = time.perf_counter()

        student_token = await exchange_token(authorization)
        goal, student = await asyncio.gather(
            get_learning_goal(goal_id, student_token),
            get_student_profile(student_token),
        )
        module_id = goal.get("moduleId")
        if not module_id:
            raise HTTPException(
                status_code=422,
                detail=f"Learning goal {goal_id} has no moduleId",
            )
        module, tutors = await asyncio.gather(
            get_module(module_id, authorization),
            list_tutors(
                module_id=module_id,
                languages=student.get("languages", []),
                authorization=authorization,
            ),
        )

        student_langs = {lang.lower() for lang in student.get("languages", [])}
        if student_langs:
            matched = [
                t
                for t in tutors
                if {lang.lower() for lang in t.get("languages", [])} & student_langs
            ]
            if not matched:
                raise HTTPException(
                    status_code=422,
                    detail=(
                        "No tutors available for the requested language(s):"
                        f" {', '.join(student.get('languages', []))}"
                    ),
                )
            tutors = matched

        goal_locations = {loc.lower() for loc in goal.get("locations", [])}
        if goal_locations:
            matched = [
                t
                for t in tutors
                if {loc.lower() for loc in t.get("locations", [])} & goal_locations
            ]
            if not matched:
                raise HTTPException(
                    status_code=422,
                    detail=(
                        "No tutors available in the requested location(s):"
                        f" {', '.join(goal.get('locations', []))}"
                    ),
                )
            tutors = matched

        prompt = build_prompt(student, goal, module, tutors)

        # Prefer structured output (function calling) when the provider supports it —
        # OpenAI and Logos enforce the schema at the API level so the response is always
        # a valid GeneratePlanResponse with no parsing needed. Ollama and LM Studio may
        # not support tool use depending on the loaded model, so we fall back to plain
        # text generation + fence stripping + one retry for those cases.
        llm = get_llm()
        if get_llm_info()["provider"] in ("openai",):
            structured = llm.with_structured_output(SharedAiGeneratePlanResponse)
            result: SharedAiGeneratePlanResponse = await structured.ainvoke(
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
        try:
            return _map_response(data)
        except (ValidationError, KeyError) as exc:
            logger.error(
                "generate_plan response mapping failed goal_id=%s error=%s",
                goal_id,
                exc,
            )
            raise HTTPException(
                status_code=500,
                detail="LLM response did not match the expected schema",
            ) from exc

    async def _invoke_llm(self, prompt: str, goal_id: str, attempt: int = 1):
        llm_info = get_llm_info()
        try:
            return await get_llm().ainvoke([HumanMessage(content=prompt)])
        except Exception as exc:
            logger.exception(
                "generate_plan llm failed provider=%s model=%s goal_id=%s attempt=%s",
                llm_info["provider"],
                llm_info["model"],
                goal_id,
                attempt,
            )
            raise HTTPException(
                status_code=502,
                detail=f"LLM request failed: {exc}",
            ) from exc
