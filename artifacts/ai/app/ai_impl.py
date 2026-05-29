from openapi_server.apis.default_api_base import BaseDefaultApi
from openapi_server.models.test200_response import Test200Response


class DefaultApiImpl(BaseDefaultApi):
    async def test(self) -> Test200Response:
        return Test200Response(message="AI service is up")
