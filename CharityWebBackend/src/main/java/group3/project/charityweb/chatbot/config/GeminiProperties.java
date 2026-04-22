package group3.project.charityweb.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ai.gemini")
public class GeminiProperties {
    private String apiKey;
    private String baseUrl = "https://generativelanguage.googleapis.com";
    private String model = "gemini-2.5-flash";
    private String fallbackModel = "gemini-1.5-flash";
    private String fallbackModels = "gemini-flash-lite-latest,gemini-2.0-flash-lite,gemini-2.0-flash";
    private double temperature = 0.2;
    private int maxOutputTokens = 768;
    private boolean retryEnabled = true;
    private int retryMaxAttempts = 3;
    private long retryInitialBackoffMs = 400;
}




