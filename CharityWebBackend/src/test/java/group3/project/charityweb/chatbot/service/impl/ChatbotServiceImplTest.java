package group3.project.charityweb.chatbot.service.impl;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.config.ChatbotProperties;
import group3.project.charityweb.chatbot.config.GeminiProperties;
import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatSource;
import group3.project.charityweb.chatbot.exception.ChatbotRateLimitException;
import group3.project.charityweb.chatbot.exception.ChatbotUpstreamException;
import group3.project.charityweb.chatbot.exception.ChatbotValidationException;
import group3.project.charityweb.chatbot.service.*;
import group3.project.charityweb.chatbot.service.model.ChatContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatbotServiceImplTest {

    @Mock
    private ContextBuilderService contextBuilderService;
    @Mock
    private PromptTemplateService promptTemplateService;
    @Mock
    private GeminiClient geminiClient;
    @Mock
    private SuggestionService suggestionService;
    @Mock
    private ChatbotRateLimiterService chatbotRateLimiterService;

    private final ChatbotProperties chatbotProperties = new ChatbotProperties();
    private final GeminiProperties geminiProperties = new GeminiProperties();

    @InjectMocks
    private ChatbotServiceImpl chatbotService;

    @BeforeEach
    void setUp() {
        chatbotProperties.setStrictUpstream(false);
        chatbotProperties.setMaxQuestionLength(1000);
        chatbotProperties.setMaxOutputChars(2000);
        geminiProperties.setModel("gemini-1.5-flash");

        chatbotService = new ChatbotServiceImpl(
                contextBuilderService,
                promptTemplateService,
                geminiClient,
                suggestionService,
                chatbotRateLimiterService,
                chatbotProperties,
                geminiProperties
        );

        lenient().when(chatbotRateLimiterService.allow(anyString(), org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(true);
        lenient().when(contextBuilderService.build(org.mockito.ArgumentMatchers.any())).thenReturn(
                ChatContext.builder()
                        .text("context")
                        .sources(List.of(ChatSource.builder().type("PROJECT").id("p1").title("Du an A").build()))
                        .build());
        lenient().when(promptTemplateService.buildSystemPrompt(org.mockito.ArgumentMatchers.any())).thenReturn("system");
        lenient().when(promptTemplateService.buildUserPrompt(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn("user");
    }

    @Test
    void ask_whenUpstreamFails_shouldReturnFallbackInNonStrictMode() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Lam sao de donate?");

        when(geminiClient.generate("system", "user")).thenThrow(new ChatbotUpstreamException("upstream error"));

        var response = chatbotService.ask(request, "IP:127.0.0.1", false);

        assertNotNull(response);
        assertTrue(response.getMeta().isFallbackUsed());
        assertNotNull(response.getSafetyNotice());
        assertNotNull(response.getConversationId());
        assertFalse(response.getSources().isEmpty());
    }

    @Test
    void ask_whenQuestionIsBlank_shouldThrowValidationException() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("  ");

        assertThrows(ChatbotValidationException.class, () -> chatbotService.ask(request, "IP:127.0.0.1", false));
    }

    @Test
    void ask_whenRateLimitExceeded_shouldThrowRateLimitException() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("hello");

        when(chatbotRateLimiterService.allow(anyString(), org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(false);

        assertThrows(ChatbotRateLimitException.class, () -> chatbotService.ask(request, "IP:127.0.0.1", false));
    }
}
