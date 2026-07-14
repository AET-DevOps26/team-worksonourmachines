import logging
import os

import httpx
from fastapi import HTTPException

logger = logging.getLogger(__name__)

STUDENT_API_URL = os.getenv("STUDENT_API_URL", "http://server-student:8081")
MARKETPLACE_API_URL = os.getenv("MARKETPLACE_API_URL", "http://server-marketplace:8082")

_client = httpx.AsyncClient(timeout=10.0)


def _auth_headers(authorization: str) -> dict:
    return {"Authorization": authorization}


def _raise_for_status(response: httpx.Response, context: str) -> None:
    if response.is_success:
        return
    logger.error(
        "%s failed status=%s body=%s", context, response.status_code, response.text
    )
    if response.status_code == 404:
        raise HTTPException(status_code=404, detail=f"{context}: not found")
    if response.status_code == 401:
        raise HTTPException(status_code=401, detail="Unauthorized")
    raise HTTPException(status_code=502, detail=f"Upstream service error ({context})")


async def get_learning_goal(goal_id: str, authorization: str) -> dict:
    url = f"{STUDENT_API_URL}/v1/students/me/goals/{goal_id}"
    response = await _client.get(url, headers=_auth_headers(authorization))
    _raise_for_status(response, f"get_learning_goal goal_id={goal_id}")
    return response.json()


async def get_student_profile(authorization: str) -> dict:
    url = f"{STUDENT_API_URL}/v1/students/me"
    response = await _client.get(url, headers=_auth_headers(authorization))
    _raise_for_status(response, "get_student_profile")
    return response.json()


async def get_module(module_id: str, authorization: str) -> dict:
    # Two-step: list to resolve UUID → code, then fetch by code to get topics.
    url = f"{MARKETPLACE_API_URL}/v1/modules"
    response = await _client.get(
        url, params={"pageSize": 100}, headers=_auth_headers(authorization)
    )
    _raise_for_status(response, f"get_module module_id={module_id}")
    data = response.json()
    items = data.get("items", data) if isinstance(data, dict) else data
    summary = next((item for item in items if item.get("id") == module_id), None)
    if summary is None:
        raise HTTPException(status_code=404, detail=f"Module {module_id} not found")
    code = summary.get("code")
    if not code:
        return summary
    response = await _client.get(
        f"{MARKETPLACE_API_URL}/v1/modules/{code}",
        headers=_auth_headers(authorization),
    )
    _raise_for_status(response, f"get_module code={code}")
    return response.json()


async def list_tutors(
    module_id: str,
    languages: list,
    authorization: str,
) -> list:
    params: dict = {"moduleId": module_id}
    if languages:
        params["languages"] = languages
    url = f"{MARKETPLACE_API_URL}/v1/tutors"
    response = await _client.get(
        url, params=params, headers=_auth_headers(authorization)
    )
    _raise_for_status(response, f"list_tutors module_id={module_id}")
    data = response.json()
    return data.get("items", data) if isinstance(data, dict) else data
