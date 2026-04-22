package group3.project.charityweb.chatbot.service.model;

import group3.project.charityweb.chatbot.dto.response.ChatSource;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatContext {
    private String text;
    private List<ChatSource> sources;
}

