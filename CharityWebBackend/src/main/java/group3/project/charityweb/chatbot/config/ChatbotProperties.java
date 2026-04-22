package group3.project.charityweb.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ai.chatbot")
public class ChatbotProperties {
    private int timeoutMs = 15000;
    private int maxQuestionLength = 1000;
    private int maxContextProjects = 8;
    private int maxContextActivities = 5;
    private int maxProjectDescriptionChars = 800;
    private int maxActivityContentChars = 600;
    private int maxOutputChars = 2000;
    private boolean strictUpstream = false;
    private int anonymousRateLimit = 20;
    private int authenticatedRateLimit = 60;
    private int rateWindowSeconds = 300;
}



