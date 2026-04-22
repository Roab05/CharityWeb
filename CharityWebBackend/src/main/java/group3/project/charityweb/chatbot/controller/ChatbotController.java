package group3.project.charityweb.chatbot.controller;

import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatAskResponse;
import group3.project.charityweb.chatbot.dto.response.ChatSuggestionResponse;
import group3.project.charityweb.chatbot.service.ChatbotService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<ChatAskResponse> ask(
            @RequestBody ChatAskRequest request,
            Principal principal,
            HttpServletRequest httpServletRequest) {
        boolean authenticated = principal != null;
        String clientId = authenticated
                ? "USER:" + principal.getName()
                : "IP:" + httpServletRequest.getRemoteAddr();

        return ResponseEntity.ok(chatbotService.ask(request, clientId, authenticated));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ChatSuggestionResponse> getSuggestions(
            @RequestParam(required = false) String projectId) {
        return ResponseEntity.ok(chatbotService.getSuggestions(projectId));
    }
}

