# Ludii + C3PO + LLM Integration

This repository demonstrates how to integrate **Ludii** (a general game playing engine) with an LLM-based agent to make gameplay decisions.  
The architecture is composed of:

- **Ludii**: runs the game and enforces rules.
- **C-3PO (Java)**: a custom AI agent implementing Ludii's `AI` interface.
- **LangChain4j + Quarkus**: Java framework to interact with LLMs, both online (e.g., OpenAI GPT) and offline (e.g., Ollama).
- **LLM**: a language model used to choose moves based on the current game state.

---

## 🧠 Architecture

```mermaid
graph LR
  subgraph "LUDII Engine"
    LUDII["JAR: Ludii"]
    AGENT["JAR: C-3PO (AI agent)"]
    LUDII --> AGENT
  end

  AGENT -- "selectAction()" --> LANGCHAIN["LangChain4j + Quarkus"]
  LANGCHAIN -- "API request" --> LLM["LLM (GPT, Ollama...)"]
  LLM -- "suggested move" --> LANGCHAIN
  LANGCHAIN --> AGENT
```

---

## ⚙️ How It Works

1. Ludii starts a match and periodically calls the `selectAction()` method on the C3PO bot.
2. C3PO accesses the current game state (via `Context`, `Game`, `Move`, etc.) and builds a **textual prompt** describing the situation.
3. This prompt is passed to LangChain4j, which makes an **API request** to an LLM.
4. The model replies with the **move to play** (e.g., "move from E2 to E4").
5. C3PO translates this response into a valid `Move` object (or the closest match among the pseudo-legal options).
6. The move is returned to Ludii.

---

## 🔌 Support for Local and Remote LLMs

You can configure the agent to use:

* 🌐 **Remote models**: like GPT-4 via OpenAI, Anthropic, Mistral on OpenRouter, etc.
* 💻 **Local models**: like **Ollama**, running LLMs locally (e.g., `mistral`, `llama3`, `codellama`).

Configure the LangChain4j backend in `application.properties`:

```properties
llm.provider=openai

# or for Ollama
llm.provider=ollama
llm.ollama.model=llama3
llm.ollama.host=http://localhost:11434
```

---

## 🔧 Build & Run

_Coming soon_

---

## 📁 TODO

- [ ] 
- [ ] 
