package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InteractionResponse {
    private String interactionId;
    private Integer type;
    private String content;
    private LocalDateTime createdAt;

    // Thông tin người tương tác để hiển thị lên UI
    private String userId;
    private String username;
    private String displayName; // Tên đầy đủ của Cá nhân hoặc Tên Tổ chức
}