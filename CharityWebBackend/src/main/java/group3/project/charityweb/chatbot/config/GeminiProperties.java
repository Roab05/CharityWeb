package group3.project.charityweb.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ai.gemini")
public class GeminiProperties {
    private String apiKey;
    private String baseUrl = "https://generativelanguage.googleapis.com";
    private String model = "gemini-1.5-flash";
    private double temperature = 0.2;
    private int maxOutputTokens = 512;
}

