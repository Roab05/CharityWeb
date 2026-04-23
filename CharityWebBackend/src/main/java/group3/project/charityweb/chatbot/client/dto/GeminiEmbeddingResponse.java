package group3.project.charityweb.chatbot.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiEmbeddingResponse {
    private Embedding embedding;

    @Data
    public static class Embedding {
        private List<Float> values;
    }
}
