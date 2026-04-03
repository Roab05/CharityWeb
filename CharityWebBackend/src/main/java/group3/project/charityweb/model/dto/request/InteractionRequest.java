package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class InteractionRequest {
    private Integer type; // 1 cho Comment, 2 cho Like
    private String content; // Có thể null nếu type = 2 (Like)
}