FROM python:3.12-slim AS base

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

COPY artifacts/ai/requirements.txt .

RUN --mount=type=cache,id=ai-pip,target=/root/.cache/pip \
    pip install -r requirements.txt

# -----------------------------

FROM base AS dev

EXPOSE 8000

CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]

# -----------------------------

FROM base AS tooling

COPY artifacts/ai/docker/tooling-entrypoint.sh /usr/local/bin/tooling-entrypoint
RUN chmod +x /usr/local/bin/tooling-entrypoint

WORKDIR /workspace/artifacts/ai

ENTRYPOINT ["tooling-entrypoint"]
CMD ["run", "lint"]

# -----------------------------

FROM base AS prod

RUN adduser --disabled-password --gecos "" appuser

COPY artifacts/ai .

RUN chown -R appuser /app

USER appuser

EXPOSE 8000

CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
