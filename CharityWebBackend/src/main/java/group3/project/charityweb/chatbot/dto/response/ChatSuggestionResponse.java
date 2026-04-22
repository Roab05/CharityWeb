package group3.project.charityweb.chatbot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatSuggestionResponse {
    private List<String> items;
}

