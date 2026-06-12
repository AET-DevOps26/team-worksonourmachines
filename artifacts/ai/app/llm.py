import os

from langchain_openai import ChatOpenAI

# Module-level singleton — created once on first call, reused for all requests.
_llm = None
_provider: str = "lmstudio"
_model: str = ""


def _build_llm() -> ChatOpenAI:
    global _provider, _model
    _provider = os.getenv("LLM_PROVIDER", "lmstudio").lower()
    _model = os.getenv(
        "LLM_MODEL",
        "openai/gpt-oss-120b" if _provider == "logos" else "local-model",
    )
    return ChatOpenAI(
        base_url=os.environ["LLM_BASE_URL"],
        # LM Studio/OpenWebUI don't require a real key but reject an empty value.
        api_key=os.getenv("LLM_API_KEY", "not-required"),
        model=_model,
    )


def get_llm() -> ChatOpenAI:
    global _llm
    if _llm is None:
        _llm = _build_llm()
    return _llm


def get_llm_info() -> dict:
    # Ensures _provider and _model are populated before returning them.
    get_llm()
    return {"provider": _provider, "model": _model}
