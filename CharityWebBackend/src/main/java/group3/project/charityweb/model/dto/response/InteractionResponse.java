package group3.project.charityweb.model.dto.response;

import group3.project.charityweb.model.enums.InteractionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InteractionResponse {
    private String interactionId;
    private InteractionType type;
    private String content;
    private LocalDateTime createdAt;

    private String userId;
    private String username;
    private String displayName;
}