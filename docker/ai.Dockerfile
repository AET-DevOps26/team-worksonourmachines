FROM python:3.12-slim AS base

WORKDIR /app

COPY artifacts/ai/requirements.txt .

# -----------------------------

FROM base AS dev

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

RUN --mount=type=cache,id=ai-pip,target=/root/.cache/pip \
    pip install -r requirements.txt

EXPOSE 8000

CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]

# -----------------------------

FROM base AS prod

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

RUN --mount=type=cache,id=ai-pip,target=/root/.cache/pip \
    pip install -r requirements.txt

COPY artifacts/ai .

EXPOSE 8000

CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
