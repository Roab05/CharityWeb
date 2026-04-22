package group3.project.charityweb.chatbot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSource {
    private String type;
    private String id;
    private String title;
}

