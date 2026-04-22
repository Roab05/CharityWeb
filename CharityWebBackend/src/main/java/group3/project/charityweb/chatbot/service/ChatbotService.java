package group3.project.charityweb.chatbot.service;

import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatAskResponse;
import group3.project.charityweb.chatbot.dto.response.ChatSuggestionResponse;

public interface ChatbotService {
    ChatAskResponse ask(ChatAskRequest request, String clientId, boolean authenticated);
    ChatSuggestionResponse getSuggestions(String projectId);
}

