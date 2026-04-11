package group3.project.charityweb.model.dto.request;

import group3.project.charityweb.model.enums.InteractionType;
import lombok.Data;

@Data
public class InteractionRequest {
    private InteractionType type; // 1 cho Comment, 2 cho Like
    private String content; // Có thể null nếu type = 2 (Like)
}