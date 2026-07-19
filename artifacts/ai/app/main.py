import logging
import os

from fastapi import FastAPI
from openapi_server.apis.default_api import router as v1_router
from openapi_server.security_api import get_token_KeycloakClientAuth
from prometheus_fastapi_instrumentator import Instrumentator

import app.ai_impl  # noqa: F401 — registers DefaultApiImpl with BaseDefaultApi
from app.auth import verify_service_token

logging.basicConfig(
    level=os.getenv("LOG_LEVEL", "INFO").upper(),
    format="%(asctime)s %(levelname)s %(name)s — %(message)s",
)

app = FastAPI(title="TutorMatch AI Service", version="0.0.1")

# Replace the no-op generated security function with real JWT validation.
app.dependency_overrides[get_token_KeycloakClientAuth] = verify_service_token

Instrumentator().instrument(app).expose(app)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


app.include_router(v1_router)
