package group3.project.charityweb.chatbot.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponse {
    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;

    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }

    @Data
    public static class UsageMetadata {
        private Integer promptTokenCount;
        private Integer candidatesTokenCount;
        private Integer totalTokenCount;
    }
}


