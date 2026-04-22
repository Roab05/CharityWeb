package group3.project.charityweb.chatbot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatAskResponse {
    private String answer;
    private String conversationId;
    private List<ChatSource> sources;
    private String safetyNotice;
    private ChatMeta meta;
}

