import logging
import os

from fastapi import FastAPI

logging.basicConfig(
    level=os.getenv("LOG_LEVEL", "INFO").upper(),
    format="%(asctime)s %(levelname)s %(name)s — %(message)s",
)

app = FastAPI(title="TutorMatch AI Service", version="0.0.1")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}
