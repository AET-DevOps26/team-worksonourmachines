import os

from langchain_ollama import ChatOllama
from langchain_openai import ChatOpenAI

_llm = None
_provider: str = "ollama"
_model: str = ""


def get_llm():
    global _llm, _provider, _model
    if _llm is None:
        _provider = os.getenv("LLM_PROVIDER", "ollama").lower()
        if _provider in ("lmstudio", "openwebui"):
            _model = os.getenv("LLM_MODEL", "local-model")
            _llm = ChatOpenAI(
                base_url=os.environ["LLM_BASE_URL"],
                api_key="not-required",
                model=_model,
            )
        elif _provider == "logos":
            _model = os.getenv("LLM_MODEL", "openai/gpt-oss-120b")
            _llm = ChatOpenAI(
                base_url=os.environ["LLM_BASE_URL"],
                api_key=os.environ["LLM_API_KEY"],
                model=_model,
            )
        else:
            _provider = "ollama"
            _model = os.environ["OLLAMA_MODEL"]
            _llm = ChatOllama(
                base_url=os.environ["OLLAMA_BASE_URL"],
                model=_model,
            )
    return _llm


def get_llm_info() -> dict:
    get_llm()
    return {"provider": _provider, "model": _model}
