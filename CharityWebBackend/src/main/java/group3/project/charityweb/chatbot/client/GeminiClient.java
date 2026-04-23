package group3.project.charityweb.chatbot.client;

import group3.project.charityweb.chatbot.client.dto.GeminiGenerationResult;

public interface GeminiClient {
    GeminiGenerationResult generate(String systemPrompt, String userPrompt);
}


