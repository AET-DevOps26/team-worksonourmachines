import json
from contextlib import ExitStack
from unittest.mock import AsyncMock, MagicMock, patch

import pytest
from fastapi import HTTPException
from openapi_server.models.generate_plan_request import GeneratePlanRequest
from openapi_server.models.generate_plan_response import GeneratePlanResponse

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
async def test_generate_plan_uses_structured_output_for_openai(mock_clients):
    expected = _map_response(VALID_RESPONSE)
    with (
        patch("app.ai_impl.get_llm") as mock_get_llm,
        patch(
            "app.ai_impl.get_llm_info",
            return_value={"provider": "openai", "model": "gpt-4o"},
        ),
    ):
        structured_llm = AsyncMock()
        structured_llm.ainvoke = AsyncMock(return_value=expected)
        llm = MagicMock()
        llm.with_structured_output = MagicMock(return_value=structured_llm)
        mock_get_llm.return_value = llm

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        result = await impl.generate_plan(req, "Bearer tok")

    # with_structured_output called with the response model
    llm.with_structured_output.assert_called_once_with(GeneratePlanResponse)
    # ainvoke called exactly once — no fallback needed
    structured_llm.ainvoke.assert_called_once()
    assert result.learning_goal_id == "goal-1"


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


# ---------------------------------------------------------------------------
# Language / location filtering
# ---------------------------------------------------------------------------

TUTOR_DE = {
    "id": "t-de",
    "displayName": "Hans Müller",
    "hourlyRate": 20,
    "languages": ["German"],
    "locations": ["garching"],
    "ratingSummary": {"average": 4.5, "count": 5},
    "availability": [],
    "coverages": [],
}

TUTOR_EN = {
    "id": "t-en",
    "displayName": "Jane Smith",
    "hourlyRate": 30,
    "languages": ["English"],
    "locations": ["online"],
    "ratingSummary": {"average": 4.9, "count": 10},
    "availability": [],
    "coverages": [],
}

TUTOR_BOTH = {
    "id": "t-both",
    "displayName": "Maria Garcia",
    "hourlyRate": 25,
    "languages": ["German", "English"],
    "locations": ["munich", "online"],
    "ratingSummary": {"average": 4.7, "count": 8},
    "availability": [],
    "coverages": [],
}


def _filtering_mock_clients(student_override=None, goal_override=None, tutors=None):
    student = {**STUDENT, **(student_override or {})}
    goal = {**GOAL, **(goal_override or {})}
    raw_tutors = tutors if tutors is not None else [TUTOR_DE, TUTOR_EN, TUTOR_BOTH]
    return [
        patch(
            "app.ai_impl.get_learning_goal",
            new_callable=AsyncMock,
            return_value=goal,
        ),
        patch(
            "app.ai_impl.get_student_profile",
            new_callable=AsyncMock,
            return_value=student,
        ),
        patch("app.ai_impl.get_module", new_callable=AsyncMock, return_value=MODULE),
        patch(
            "app.ai_impl.list_tutors",
            new_callable=AsyncMock,
            return_value=raw_tutors,
        ),
        patch(
            "app.ai_impl.get_llm_info",
            return_value={"provider": "test", "model": "test"},
        ),
    ]


def _mock_llm_returning(json_str):
    llm = AsyncMock()
    llm.ainvoke = AsyncMock(return_value=_make_llm_message(json_str))
    return llm


@pytest.mark.asyncio
async def test_language_filter_passes_only_matching_tutors_to_llm():
    # Student speaks only German — English-only tutor should be excluded.
    with ExitStack() as stack:
        for p in _filtering_mock_clients(student_override={"languages": ["German"]}):
            stack.enter_context(p)
        mock_build_prompt = stack.enter_context(patch("app.ai_impl.build_prompt"))
        mock_get_llm = stack.enter_context(patch("app.ai_impl.get_llm"))
        mock_build_prompt.return_value = "prompt"
        mock_get_llm.return_value = _mock_llm_returning(MOCK_LLM_RESPONSE)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        await impl.generate_plan(req, "Bearer tok")

    _, _, _, tutors_arg = mock_build_prompt.call_args.args
    tutor_ids = {t["id"] for t in tutors_arg}
    assert "t-de" in tutor_ids
    assert "t-both" in tutor_ids
    assert "t-en" not in tutor_ids


@pytest.mark.asyncio
async def test_language_filter_case_insensitive():
    with ExitStack() as stack:
        for p in _filtering_mock_clients(student_override={"languages": ["german"]}):
            stack.enter_context(p)
        mock_build_prompt = stack.enter_context(patch("app.ai_impl.build_prompt"))
        mock_get_llm = stack.enter_context(patch("app.ai_impl.get_llm"))
        mock_build_prompt.return_value = "prompt"
        mock_get_llm.return_value = _mock_llm_returning(MOCK_LLM_RESPONSE)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        await impl.generate_plan(req, "Bearer tok")

    _, _, _, tutors_arg = mock_build_prompt.call_args.args
    assert any(t["id"] == "t-de" for t in tutors_arg)


@pytest.mark.asyncio
async def test_language_filter_no_match_raises_422():
    with ExitStack() as stack:
        for p in _filtering_mock_clients(student_override={"languages": ["French"]}):
            stack.enter_context(p)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        with pytest.raises(HTTPException) as exc_info:
            await impl.generate_plan(req, "Bearer tok")

    assert exc_info.value.status_code == 422
    assert "language" in exc_info.value.detail.lower()
    assert "French" in exc_info.value.detail


@pytest.mark.asyncio
async def test_language_filter_skipped_when_student_has_no_languages():
    with ExitStack() as stack:
        for p in _filtering_mock_clients(student_override={"languages": []}):
            stack.enter_context(p)
        mock_build_prompt = stack.enter_context(patch("app.ai_impl.build_prompt"))
        mock_get_llm = stack.enter_context(patch("app.ai_impl.get_llm"))
        mock_build_prompt.return_value = "prompt"
        mock_get_llm.return_value = _mock_llm_returning(MOCK_LLM_RESPONSE)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        await impl.generate_plan(req, "Bearer tok")

    _, _, _, tutors_arg = mock_build_prompt.call_args.args
    assert len(tutors_arg) == 3


@pytest.mark.asyncio
async def test_location_filter_passes_only_matching_tutors_to_llm():
    # Goal prefers garching — online-only and munich tutors should be excluded.
    with ExitStack() as stack:
        for p in _filtering_mock_clients(goal_override={"locations": ["garching"]}):
            stack.enter_context(p)
        mock_build_prompt = stack.enter_context(patch("app.ai_impl.build_prompt"))
        mock_get_llm = stack.enter_context(patch("app.ai_impl.get_llm"))
        mock_build_prompt.return_value = "prompt"
        mock_get_llm.return_value = _mock_llm_returning(MOCK_LLM_RESPONSE)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        await impl.generate_plan(req, "Bearer tok")

    _, _, _, tutors_arg = mock_build_prompt.call_args.args
    tutor_ids = {t["id"] for t in tutors_arg}
    assert "t-de" in tutor_ids
    assert "t-en" not in tutor_ids
    assert "t-both" not in tutor_ids


@pytest.mark.asyncio
async def test_location_filter_no_match_raises_422():
    with ExitStack() as stack:
        for p in _filtering_mock_clients(
            goal_override={"locations": ["weihenstephan"]}
        ):
            stack.enter_context(p)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        with pytest.raises(HTTPException) as exc_info:
            await impl.generate_plan(req, "Bearer tok")

    assert exc_info.value.status_code == 422
    assert "location" in exc_info.value.detail.lower()
    assert "weihenstephan" in exc_info.value.detail


@pytest.mark.asyncio
async def test_location_filter_skipped_when_goal_has_no_locations():
    with ExitStack() as stack:
        for p in _filtering_mock_clients(goal_override={"locations": []}):
            stack.enter_context(p)
        mock_build_prompt = stack.enter_context(patch("app.ai_impl.build_prompt"))
        mock_get_llm = stack.enter_context(patch("app.ai_impl.get_llm"))
        mock_build_prompt.return_value = "prompt"
        mock_get_llm.return_value = _mock_llm_returning(MOCK_LLM_RESPONSE)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        await impl.generate_plan(req, "Bearer tok")

    _, _, _, tutors_arg = mock_build_prompt.call_args.args
    assert len(tutors_arg) == 3


@pytest.mark.asyncio
async def test_both_filters_applied_sequentially():
    # Student: German only. Goal: online only.
    # TUTOR_DE:   German / garching       → passes language, fails location
    # TUTOR_EN:   English / online        → fails language
    # TUTOR_BOTH: German+English / munich+online → passes both
    with ExitStack() as stack:
        for p in _filtering_mock_clients(
            student_override={"languages": ["German"]},
            goal_override={"locations": ["online"]},
        ):
            stack.enter_context(p)
        mock_build_prompt = stack.enter_context(patch("app.ai_impl.build_prompt"))
        mock_get_llm = stack.enter_context(patch("app.ai_impl.get_llm"))
        mock_build_prompt.return_value = "prompt"
        mock_get_llm.return_value = _mock_llm_returning(MOCK_LLM_RESPONSE)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        await impl.generate_plan(req, "Bearer tok")

    _, _, _, tutors_arg = mock_build_prompt.call_args.args
    assert [t["id"] for t in tutors_arg] == ["t-both"]


@pytest.mark.asyncio
async def test_location_filter_422_after_language_filter_narrows_pool():
    # After language filter only TUTOR_DE (garching) remains.
    # Goal asks for online → no match → 422.
    with ExitStack() as stack:
        for p in _filtering_mock_clients(
            student_override={"languages": ["German"]},
            goal_override={"locations": ["online"]},
            tutors=[TUTOR_DE],
        ):
            stack.enter_context(p)

        impl = DefaultApiImpl()
        req = GeneratePlanRequest.from_dict({"learningGoalId": "goal-1"})
        with pytest.raises(HTTPException) as exc_info:
            await impl.generate_plan(req, "Bearer tok")

    assert exc_info.value.status_code == 422
    assert "location" in exc_info.value.detail.lower()
