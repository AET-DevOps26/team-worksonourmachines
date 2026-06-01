from langchain_core.messages import HumanMessage

from app.llm import get_llm
from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.chat200_response import Chat200Response
from openapi_server.models.test200_response import Test200Response


class DefaultApiImpl(BaseDefaultApi):
    async def test(self) -> Test200Response:
        # Sends a fixed prompt to the local model to verify the Ollama connection is live.
        # ainvoke() is the async variant — required for use inside FastAPI async handlers.
        response = await get_llm().ainvoke([HumanMessage(content="Say hello in one sentence.")])
        # response.content holds the plain text string returned by the model.
        return Test200Response(message=response.content)

    async def chat(self, prompt: str) -> Chat200Response:
        # Forwards the user-supplied prompt to the local model and returns its reply.
        response = await get_llm().ainvoke([HumanMessage(content=prompt)])
        return Chat200Response(message=response.content)
