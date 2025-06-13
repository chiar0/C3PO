package com.ai.ludii;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import com.ai.util.PropertiesLoader;

import java.time.Duration;

public class LLMProvider {

    public static ChatLanguageModel createModel() {
        String provider = PropertiesLoader.getProperty("llm.provider");
        
        if ("openai".equalsIgnoreCase(provider)) {
            return OpenAiChatModel.builder()
                .apiKey(PropertiesLoader.getProperty("llm.openai.api-key"))
                .modelName("gpt-4")
                .timeout(Duration.ofSeconds(120))
                .build();
        } else {
            return OllamaChatModel.builder()
                .baseUrl(PropertiesLoader.getProperty("llm.ollama.host", "http://localhost:11434")) // Fixed
                .modelName(PropertiesLoader.getProperty("llm.ollama.model", "qwen3:4b-fp16")) // Fixed
                .timeout(Duration.ofSeconds(120))
                .build();
        }
    }
}