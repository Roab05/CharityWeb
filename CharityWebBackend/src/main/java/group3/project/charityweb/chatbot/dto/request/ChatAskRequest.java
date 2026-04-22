package group3.project.charityweb.chatbot.dto.request;

import lombok.Data;

@Data
public class ChatAskRequest {
    private String question;
    private String projectId;
    private String conversationId;
    private String locale;
}

