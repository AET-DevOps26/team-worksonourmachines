import logging
import os

from fastapi import FastAPI
from openapi_server.apis.default_api import router as v1_router

import app.ai_impl  # noqa: F401 — registers DefaultApiImpl with BaseDefaultApi

logging.basicConfig(
    level=os.getenv("LOG_LEVEL", "INFO").upper(),
    format="%(asctime)s %(levelname)s %(name)s — %(message)s",
)

app = FastAPI(title="TutorMatch AI Service", version="0.0.1")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


app.include_router(v1_router)
