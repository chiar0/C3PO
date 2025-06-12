package com.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface MyAiService {

    @SystemMessage("You are a helpful assistant")
    @UserMessage("Tell me a fun fact about {topic}")
    String funFact(String topic);
    
    @SystemMessage("You are a helpful assistant")
    @UserMessage("{message}")
    String generateText(String message);
}