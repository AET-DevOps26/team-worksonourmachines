import os

from langchain_openai import ChatOpenAI

_llm = None
_provider: str = "lmstudio"
_model: str = ""


def get_llm():
    global _llm, _provider, _model
    if _llm is None:
        _provider = os.getenv("LLM_PROVIDER", "lmstudio").lower()
        _model = os.getenv(
            "LLM_MODEL",
            "openai/gpt-oss-120b" if _provider == "logos" else "local-model",
        )
        _llm = ChatOpenAI(
            base_url=os.environ["LLM_BASE_URL"],
            api_key=os.getenv("LLM_API_KEY", "not-required"),
            model=_model,
        )
    return _llm


def get_llm_info() -> dict:
    get_llm()
    return {"provider": _provider, "model": _model}
