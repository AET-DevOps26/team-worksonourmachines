import logging
import os

import httpx
from fastapi import HTTPException

logger = logging.getLogger(__name__)

STUDENT_API_URL = os.getenv("STUDENT_API_URL", "http://server-student:8081")
MARKETPLACE_API_URL = os.getenv("MARKETPLACE_API_URL", "http://server-marketplace:8082")


def _auth_headers(authorization: str) -> dict:
    return {"Authorization": authorization}


def _raise_for_status(response: httpx.Response, context: str) -> None:
    if response.is_success:
        return
    logger.error(
        "%s failed status=%s body=%s", context, response.status_code, response.text
    )
    raise HTTPException(status_code=response.status_code, detail=response.text)


async def get_learning_goal(goal_id: str, authorization: str) -> dict:
    url = f"{STUDENT_API_URL}/v1/students/me/goals/{goal_id}"
    async with httpx.AsyncClient() as client:
        response = await client.get(url, headers=_auth_headers(authorization))
    _raise_for_status(response, f"get_learning_goal goal_id={goal_id}")
    return response.json()


async def get_student_profile(authorization: str) -> dict:
    url = f"{STUDENT_API_URL}/v1/students/me"
    async with httpx.AsyncClient() as client:
        response = await client.get(url, headers=_auth_headers(authorization))
    _raise_for_status(response, "get_student_profile")
    return response.json()


async def get_module(module_id: str, authorization: str) -> dict:
    url = f"{MARKETPLACE_API_URL}/v1/modules/{module_id}"
    async with httpx.AsyncClient() as client:
        response = await client.get(url, headers=_auth_headers(authorization))
    _raise_for_status(response, f"get_module module_id={module_id}")
    return response.json()


async def list_tutors(
    module_id: str,
    languages: list,
    locations: list,
    authorization: str,
) -> list:
    params: dict = {"moduleId": module_id}
    if languages:
        params["languages"] = languages
    if locations:
        params["locations"] = locations
    url = f"{MARKETPLACE_API_URL}/v1/tutors"
    async with httpx.AsyncClient() as client:
        response = await client.get(
            url, params=params, headers=_auth_headers(authorization)
        )
    _raise_for_status(response, f"list_tutors module_id={module_id}")
    data = response.json()
    return data.get("items", data) if isinstance(data, dict) else data
