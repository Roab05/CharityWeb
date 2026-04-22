package group3.project.charityweb.chatbot.service.impl;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.config.ChatbotProperties;
import group3.project.charityweb.chatbot.config.GeminiProperties;
import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatAskResponse;
import group3.project.charityweb.chatbot.dto.response.ChatMeta;
import group3.project.charityweb.chatbot.dto.response.ChatSuggestionResponse;
import group3.project.charityweb.chatbot.exception.ChatbotRateLimitException;
import group3.project.charityweb.chatbot.exception.ChatbotUpstreamException;
import group3.project.charityweb.chatbot.exception.ChatbotValidationException;
import group3.project.charityweb.chatbot.service.*;
import group3.project.charityweb.chatbot.service.model.ChatContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ContextBuilderService contextBuilderService;
    private final PromptTemplateService promptTemplateService;
    private final GeminiClient geminiClient;
    private final SuggestionService suggestionService;
    private final ChatbotRateLimiterService chatbotRateLimiterService;
    private final ChatbotProperties chatbotProperties;
    private final GeminiProperties geminiProperties;

    @Override
    public ChatAskResponse ask(ChatAskRequest request, String clientId, boolean authenticated) {
        validateRequest(request);

        if (!chatbotRateLimiterService.allow(clientId, authenticated)) {
            throw new ChatbotRateLimitException("Ban gui qua nhieu yeu cau. Vui long thu lai sau.");
        }

        Instant start = Instant.now();
        ChatContext context = contextBuilderService.build(request.getProjectId());
        String systemPrompt = promptTemplateService.buildSystemPrompt(request.getLocale());
        String userPrompt = promptTemplateService.buildUserPrompt(request.getQuestion().trim(), context.getText());

        String answer;
        boolean fallbackUsed = false;
        String safetyNotice = null;

        try {
            answer = geminiClient.generate(systemPrompt, userPrompt);
        } catch (ChatbotUpstreamException ex) {
            if (chatbotProperties.isStrictUpstream()) {
                throw ex;
            }
            fallbackUsed = true;
            safetyNotice = "AI tam thoi khong san sang, cau tra loi duoi day la thong diep huong dan mac dinh.";
            answer = "Ban co the xem danh sach du an tai /api/v1/projects, " +
                    "xem chi tiet du an tai /api/v1/projects/{projectId}, " +
                    "va tao donation tai /api/v1/projects/{projectId}/donations.";
        }

        if (answer.length() > chatbotProperties.getMaxOutputChars()) {
            answer = answer.substring(0, chatbotProperties.getMaxOutputChars()) + "...";
        }

        String conversationId = (request.getConversationId() == null || request.getConversationId().isBlank())
                ? UUID.randomUUID().toString()
                : request.getConversationId();

        long latency = Duration.between(start, Instant.now()).toMillis();
        return ChatAskResponse.builder()
                .answer(answer)
                .conversationId(conversationId)
                .sources(context.getSources())
                .safetyNotice(safetyNotice)
                .meta(ChatMeta.builder()
                        .model(geminiProperties.getModel())
                        .latencyMs(latency)
                        .fallbackUsed(fallbackUsed)
                        .build())
                .build();
    }

    @Override
    public ChatSuggestionResponse getSuggestions(String projectId) {
        return ChatSuggestionResponse.builder()
                .items(suggestionService.getSuggestions(projectId))
                .build();
    }

    private void validateRequest(ChatAskRequest request) {
        if (request == null || request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new ChatbotValidationException("question khong duoc de trong.");
        }
        if (request.getQuestion().length() > chatbotProperties.getMaxQuestionLength()) {
            throw new ChatbotValidationException("question vuot qua do dai toi da cho phep.");
        }
    }
}


