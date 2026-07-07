import json
from unittest.mock import AsyncMock, MagicMock, patch

import pytest
from fastapi import HTTPException
from openapi_server.models.generate_plan_request import GeneratePlanRequest

from app.ai_impl import DefaultApiImpl, _extract_json, _map_response
from app.prompt import build_prompt

# ---------------------------------------------------------------------------
# _extract_json
# ---------------------------------------------------------------------------


def test_extract_json_plain():
    data = {"learningGoalId": "g1", "suggestions": []}
    assert _extract_json(json.dumps(data)) == data


def test_extract_json_strips_markdown_fence():
    data = {"learningGoalId": "g1", "suggestions": []}
    wrapped = f"```json\n{json.dumps(data)}\n```"
    assert _extract_json(wrapped) == data


def test_extract_json_strips_fence_no_language():
    data = {"learningGoalId": "g1", "suggestions": []}
    wrapped = f"```\n{json.dumps(data)}\n```"
    assert _extract_json(wrapped) == data


def test_extract_json_strips_preamble():
    data = {"learningGoalId": "g1", "suggestions": []}
    # LLM sometimes adds text before the JSON block
    wrapped = f"Here is your plan:\n```json\n{json.dumps(data)}\n```"
    assert _extract_json(wrapped) == data


def test_extract_json_raises_on_invalid():
    with pytest.raises((json.JSONDecodeError, ValueError)):
        _extract_json("this is not json at all")


# ---------------------------------------------------------------------------
# _map_response
# ---------------------------------------------------------------------------

VALID_RESPONSE = {
    "learningGoalId": "goal-1",
    "suggestions": [
        {
            "tier": "cheapest",
            "description": "Cheapest plan.",
            "totalEstimatedCost": 50,
            "proposedTutors": [{"id": "u1", "displayName": "Anna", "hourlyRate": 25}],
            "milestones": [
                {
                    "title": "Topic 1",
                    "dueDate": "2026-08-01T00:00:00Z",
                    "topicId": "t1",
                    "tutorId": "u1",
                    "estimatedCost": 25,
                }
            ],
        }
    ],
}


def test_map_response_basic():
    result = _map_response(VALID_RESPONSE)
    assert result.learning_goal_id == "goal-1"
    assert len(result.suggestions) == 1
    s = result.suggestions[0]
    assert s.tier == "cheapest"
    assert s.total_estimated_cost == 50
    assert len(s.proposed_tutors) == 1
    assert s.proposed_tutors[0].display_name == "Anna"
    assert len(s.milestones) == 1
    assert s.milestones[0].title == "Topic 1"
    assert s.milestones[0].topic_id == "t1"


def test_map_response_empty_suggestions():
    result = _map_response({"learningGoalId": "g2", "suggestions": []})
    assert result.learning_goal_id == "g2"
    assert result.suggestions == []


# ---------------------------------------------------------------------------
# build_prompt
# ---------------------------------------------------------------------------

STUDENT = {
    "displayName": "Lukas Weber",
    "bio": "Looking for help.",
    "languages": ["German", "English"],
    "studyFocus": {
        "memorization": 3,
        "formalReasoning": 2,
        "conceptualUnderstanding": 4,
        "problemSolving": 3,
    },
}

GOAL = {
    "id": "goal-1",
    "moduleId": "mod-la",
    "description": "Prepare for exam.",
    "targetDate": "2026-08-15T00:00:00Z",
    "budgetEur": 80,
    "locations": ["garching", "online"],
    "selfAssessedLevel": 2,
}

MODULE = {
    "code": "LA",
    "title": "Lineare Algebra",
    "topics": [
        {
            "id": "la-t1",
            "name": "Vector spaces",
            "difficultyHint": "Medium",
            "studyFocus": {
                "memorization": 2,
                "formalReasoning": 4,
                "conceptualUnderstanding": 4,
                "problemSolving": 3,
            },
        }
    ],
}

TUTORS = [
    {
        "id": "u1",
        "displayName": "Anna Schmidt",
        "hourlyRate": 25,
        "languages": ["German", "English"],
        "locations": ["garching"],
        "ratingSummary": {"average": 4.8, "count": 20},
        "availability": [{"weekday": "monday", "available": True}],
        "coverages": [{"moduleCode": "LA"}],
    }
]


def test_build_prompt_contains_key_sections():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "## Student" in prompt
    assert "## Learning goal" in prompt
    assert "## Topics to cover" in prompt
    assert "## Available tutors" in prompt
    assert "## Instructions" in prompt
    assert "## Output format" in prompt


def test_build_prompt_injects_student_name():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "Lukas Weber" in prompt


def test_build_prompt_injects_budget():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "€80" in prompt


def test_build_prompt_injects_target_date():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "2026-08-15" in prompt


def test_build_prompt_injects_tutor():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "Anna Schmidt" in prompt


def test_build_prompt_injects_topic():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "Vector spaces" in prompt


def test_build_prompt_no_tutors_fallback():
    prompt = build_prompt(STUDENT, GOAL, MODULE, [])
    assert "No tutors available" in prompt


def test_build_prompt_goal_id_in_output_format():
    prompt = build_prompt(STUDENT, GOAL, MODULE, TUTORS)
    assert "goal-1" in prompt


# ---------------------------------------------------------------------------
# DefaultApiImpl.generate_plan
# ---------------------------------------------------------------------------

MOCK_LLM_RESPONSE = json.dumps(VALID_RESPONSE)


def _make_llm_message(content: str):
    msg = MagicMock()
    msg.content = content
    return msg


@pytest.fixture()
def mock_clients():
    with (
        patch("app.ai_impl.get_learning_goal", new_callable=AsyncMock) as gl,
        patch("app.ai_impl.get_student_profile", new_callable=AsyncMock) as gs,
        patch("app.ai_impl.get_module", new_callable=AsyncMock) as gm,
        patch("app.ai_impl.list_tutors", new_callable=AsyncMock) as lt,
        patch(
            "app.ai_impl.get_llm_info",
            return_value={"provider": "test", "model": "test"},
        ),
    ):
        gl.return_value = GOAL
        gs.return_value = STUDENT
        gm.return_value = MODULE
        lt.return_value = TUTORS
        yield gl, gs, gm, lt


@pytest.mark.asyncio
async def test_generate_plan_success(mock_clients):
    with patch("app.ai_impl.get_llm") as mock_get_llm:
        llm = AsyncMock()
        llm.ainvoke = AsyncMock(return_value=_make_llm_message(MOCK_LLM_RESPONSE))
        mock_get_llm.return_value = llm

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        result = await impl.generate_plan(req, "Bearer tok")

    assert result.learning_goal_id == "goal-1"
    assert len(result.suggestions) == 1
    assert result.suggestions[0].tier == "cheapest"


@pytest.mark.asyncio
async def test_generate_plan_strips_markdown_fence(mock_clients):
    fenced = f"```json\n{MOCK_LLM_RESPONSE}\n```"
    with patch("app.ai_impl.get_llm") as mock_get_llm:
        llm = AsyncMock()
        llm.ainvoke = AsyncMock(return_value=_make_llm_message(fenced))
        mock_get_llm.return_value = llm

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        result = await impl.generate_plan(req, "Bearer tok")

    assert result.learning_goal_id == "goal-1"


@pytest.mark.asyncio
async def test_generate_plan_retries_on_bad_json(mock_clients):
    with patch("app.ai_impl.get_llm") as mock_get_llm:
        llm = AsyncMock()
        llm.ainvoke = AsyncMock(
            side_effect=[
                _make_llm_message("Sorry, I cannot help with that."),
                _make_llm_message(MOCK_LLM_RESPONSE),
            ]
        )
        mock_get_llm.return_value = llm

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        result = await impl.generate_plan(req, "Bearer tok")

    assert llm.ainvoke.call_count == 2
    assert result.learning_goal_id == "goal-1"
    # retry prompt should contain the correction instruction
    retry_prompt = llm.ainvoke.call_args_list[1][0][0][0].content
    assert "not valid JSON" in retry_prompt


@pytest.mark.asyncio
async def test_generate_plan_raises_500_after_two_bad_responses(mock_clients):
    with patch("app.ai_impl.get_llm") as mock_get_llm:
        llm = AsyncMock()
        llm.ainvoke = AsyncMock(
            side_effect=[
                _make_llm_message("not json"),
                _make_llm_message("still not json"),
            ]
        )
        mock_get_llm.return_value = llm

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        with pytest.raises(HTTPException) as exc_info:
            await impl.generate_plan(req, "Bearer tok")

    assert exc_info.value.status_code == 500
    assert llm.ainvoke.call_count == 2
