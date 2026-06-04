from langchain_core.messages import HumanMessage
from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.chat200_response import Chat200Response

from app.llm import get_llm


class DefaultApiImpl(BaseDefaultApi):
    async def chat(self, prompt: str) -> Chat200Response:
        response = await get_llm().ainvoke([HumanMessage(content=prompt)])
        return Chat200Response(message=response.content)
