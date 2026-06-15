import logging
import time

from langchain_core.messages import HumanMessage
from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.chat_request import ChatRequest
from openapi_server.models.test200_response import Test200Response

from app.llm import get_llm, get_llm_info

logger = logging.getLogger(__name__)


class DefaultApiImpl(BaseDefaultApi):
    async def test(self) -> Test200Response:
        response = await get_llm().ainvoke(
            [HumanMessage(content="Say hello in one sentence.")]
        )
        return Test200Response(message=response.content)

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
