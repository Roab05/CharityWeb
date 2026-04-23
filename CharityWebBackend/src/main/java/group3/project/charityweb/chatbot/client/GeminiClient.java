package group3.project.charityweb.chatbot.client;

import group3.project.charityweb.chatbot.client.dto.GeminiGenerationResult;

import java.util.List;

public interface GeminiClient {
    GeminiGenerationResult generate(String systemPrompt, String userPrompt);

    List<Float> getEmbedding(String text);
}


