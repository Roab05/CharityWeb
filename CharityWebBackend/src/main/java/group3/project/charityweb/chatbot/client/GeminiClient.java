package group3.project.charityweb.chatbot.client;

public interface GeminiClient {
    String generate(String systemPrompt, String userPrompt);
}

