import os

from langchain_ollama import ChatOllama

# Module-level singleton — created once on first use, reused on subsequent calls.
_llm: ChatOllama | None = None


def get_llm() -> ChatOllama:
    """Return the shared ChatOllama instance, initializing it on first call."""
    global _llm
    if _llm is None:
        # ChatOllama is LangChain's wrapper around Ollama's /api/chat endpoint.
        # base_url points to the Ollama server (local or in-cluster via k8s Service).
        # model is the Ollama model tag to use, e.g. "llama3.2".
        _llm = ChatOllama(
            base_url=os.environ["OLLAMA_BASE_URL"],
            model=os.environ["OLLAMA_MODEL"],
        )
    return _llm
