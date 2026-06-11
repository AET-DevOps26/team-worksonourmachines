import logging
import time

from langchain_core.messages import HumanMessage
from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.chat_request import ChatRequest

from app.llm import get_llm

logger = logging.getLogger(__name__)


class DefaultApiImpl(BaseDefaultApi):
    async def chat(self, chat_request: ChatRequest) -> Chat200Response:
        prompt_length = len(chat_request.prompt)
        start = time.perf_counter()
        try:
            response = await get_llm().ainvoke(
                [HumanMessage(content=chat_request.prompt)]
            )
            latency_ms = (time.perf_counter() - start) * 1000
            response_length = len(response.content)
            logger.info(
                "chat completed",
                extra={
                    "latency_ms": round(latency_ms, 1),
                    "prompt_length": prompt_length,
                    "response_length": response_length,
                },
            )
            return Chat200Response(message=response.content)
        except Exception:
            latency_ms = (time.perf_counter() - start) * 1000
            logger.exception(
                "chat failed",
                extra={
                    "latency_ms": round(latency_ms, 1),
                    "prompt_length": prompt_length,
                },
            )
            raise
