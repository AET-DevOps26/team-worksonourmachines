import os

from langchain_ollama import ChatOllama
from langchain_openai import ChatOpenAI

_llm = None


def get_llm():
    global _llm
    if _llm is None:
        provider = os.getenv("LLM_PROVIDER", "ollama").lower()
        if provider == "lmstudio":
            _llm = ChatOpenAI(
                base_url=os.environ["LM_STUDIO_BASE_URL"],
                api_key="lm-studio",
                model=os.getenv("LM_STUDIO_MODEL", "local-model"),
            )
        else:
            _llm = ChatOllama(
                base_url=os.environ["OLLAMA_BASE_URL"],
                model=os.environ["OLLAMA_MODEL"],
            )
    return _llm
