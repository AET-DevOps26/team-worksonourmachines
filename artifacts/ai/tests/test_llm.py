import importlib
import os

import pytest

import app.llm as llm_module


def _reload(env: dict):
    """Reset singleton state and reload module with the given env vars."""
    for k in (
        "LLM_PROVIDER",
        "LLM_MODEL",
        "LLM_BASE_URL",
        "LLM_API_KEY",
    ):
        os.environ.pop(k, None)
    os.environ.update(env)
    llm_module._llm = None
    importlib.reload(llm_module)
    llm_module._llm = None


@pytest.fixture(autouse=True)
def clean_env():
    yield
    for k in (
        "LLM_PROVIDER",
        "LLM_MODEL",
        "LLM_BASE_URL",
        "LLM_API_KEY",
    ):
        os.environ.pop(k, None)
    llm_module._llm = None


def test_lmstudio():
    from langchain_openai import ChatOpenAI

    _reload(
        {
            "LLM_PROVIDER": "lmstudio",
            "LLM_BASE_URL": "http://localhost:1234/v1",
        }
    )
    llm = llm_module.get_llm()
    info = llm_module.get_llm_info()

    assert isinstance(llm, ChatOpenAI)
    assert info["provider"] == "lmstudio"
    assert info["model"] == "local-model"


def test_logos():
    from langchain_openai import ChatOpenAI

    _reload(
        {
            "LLM_PROVIDER": "logos",
            "LLM_BASE_URL": "https://logos.example.com/v1",
            "LLM_API_KEY": "test-key",
        }
    )
    llm = llm_module.get_llm()
    info = llm_module.get_llm_info()

    assert isinstance(llm, ChatOpenAI)
    assert info["provider"] == "logos"
    assert info["model"] == "openai/gpt-oss-120b"


def test_ollama():
    from langchain_ollama import ChatOllama

    _reload(
        {
            "LLM_PROVIDER": "ollama",
            "LLM_BASE_URL": "http://localhost:11434",
            "LLM_MODEL": "llama3",
        }
    )
    llm = llm_module.get_llm()
    info = llm_module.get_llm_info()

    assert isinstance(llm, ChatOllama)
    assert info["provider"] == "ollama"
    assert info["model"] == "llama3"


def test_ollama_defaults():
    from langchain_ollama import ChatOllama

    _reload({"LLM_PROVIDER": "ollama"})
    llm = llm_module.get_llm()
    info = llm_module.get_llm_info()

    assert isinstance(llm, ChatOllama)
    assert info["model"] == "llama3.2:latest"


def test_lmstudio_missing_base_url():
    _reload({"LLM_PROVIDER": "lmstudio"})
    with pytest.raises(KeyError):
        llm_module.get_llm()


def test_singleton():
    _reload(
        {
            "LLM_PROVIDER": "lmstudio",
            "LLM_BASE_URL": "http://localhost:1234/v1",
        }
    )
    assert llm_module.get_llm() is llm_module.get_llm()
