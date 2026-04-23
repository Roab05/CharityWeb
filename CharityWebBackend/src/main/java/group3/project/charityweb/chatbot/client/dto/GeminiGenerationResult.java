package group3.project.charityweb.chatbot.client.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeminiGenerationResult {
    String text;
    String finishReason;
}

