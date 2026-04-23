package group3.project.charityweb.chatbot.service.impl;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.client.dto.GeminiGenerationResult;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
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
        geminiProperties.setModel("gemini-2.5-flash");

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
        lenient().when(promptTemplateService.buildContinuationPrompt(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("continue");
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
        assertTrue(response.getAnswer().contains("/api/v1/users/me/donations"));
    }

    @Test
    void ask_whenDonationHistoryQuestionAndUpstreamFails_shouldPreferDonationsEndpoint() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Toi muon xem lai lich su ung ho cua toi");

        when(geminiClient.generate("system", "user")).thenThrow(new ChatbotUpstreamException("upstream error"));

        var response = chatbotService.ask(request, "IP:127.0.0.1", false);

        assertNotNull(response);
        assertTrue(response.getMeta().isFallbackUsed());
        assertTrue(response.getAnswer().contains("/api/v1/users/me/donations"));
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

    @Test
    void ask_whenAnswerEndsWithListNumber_shouldRetryContinuation() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Lam sao de donate?");

        when(geminiClient.generate("system", "user"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("De quyen gop, ban can lam theo cac buoc sau:\n\n1")
                        .finishReason("STOP")
                        .build());
        when(geminiClient.generate("system", "continue"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("1) Mo trang du an.\n2) Tao donation.\n3) Thanh toan va cho callback thanh cong.")
                        .finishReason("STOP")
                        .build());

        var response = chatbotService.ask(request, "IP:127.0.0.1", false);

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("1) Mo trang du an."));
        verify(geminiClient, atLeastOnce()).generate("system", "continue");
    }

    @Test
    void ask_whenFinishReasonIsMaxTokens_shouldRetryContinuationEvenIfSentenceLooksComplete() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Huong dan donate?");

        when(geminiClient.generate("system", "user"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("Ban vao trang du an, tao donation va thanh toan online.")
                        .finishReason("MAX_TOKENS")
                        .build());
        when(geminiClient.generate("system", "continue"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("Sau callback thanh cong, he thong cap nhat trang thai donation SUCCESS.")
                        .finishReason("STOP")
                        .build());

        var response = chatbotService.ask(request, "IP:127.0.0.1", false);

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("cap nhat trang thai donation SUCCESS"));
        verify(geminiClient, atLeastOnce()).generate("system", "continue");
    }

    @Test
    void ask_whenContinuationRepeatsWholeAnswer_shouldDeduplicateFinalText() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Huong dan donate?");

        String repeated = "De quyen gop cho du an Project 3, ban lam nhu sau:\n\n" +
                "1) Chon du an va tao donation.\n" +
                "2) Thanh toan VNPay.\n" +
                "3) He thong cap nhat giao dich thanh cong.\n\n" +
                "De quyen gop cho du an Project 3, ban lam nhu sau:\n\n" +
                "1) Chon du an va tao donation.\n" +
                "2) Thanh toan VNPay.\n" +
                "3) He thong cap nhat giao dich thanh cong.";

        when(geminiClient.generate("system", "user"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("De quyen gop cho du an Project 3, ban lam nhu sau:\n\n1")
                        .finishReason("STOP")
                        .build());
        when(geminiClient.generate("system", "continue"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text(repeated)
                        .finishReason("STOP")
                        .build());

        var response = chatbotService.ask(request, "IP:127.0.0.1", false);

        assertNotNull(response);
        assertEquals(1, countOccurrences(response.getAnswer(), "De quyen gop cho du an Project 3"));
        verify(geminiClient, times(1)).generate("system", "continue");
    }

    @Test
    void ask_whenFirstAnswerEndsWithTruncatedBullet_shouldRetryAndComplete() {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Huong dan quyen gop?");

        when(geminiClient.generate("system", "user"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("De quyen gop cho du an nay, ban co the thuc hien theo cac buoc sau:\n\n*   **Buoc 1:** Chon du an")
                        .finishReason("STOP")
                        .build());
        when(geminiClient.generate("system", "continue"))
                .thenReturn(GeminiGenerationResult.builder()
                        .text("1) Chon du an va tao donation.\n2) Thanh toan qua VNPay.\n3) Nhan ket qua callback thanh cong.")
                        .finishReason("STOP")
                        .build());

        var response = chatbotService.ask(request, "IP:127.0.0.1", false);

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("Thanh toan qua VNPay"));
        verify(geminiClient, atLeastOnce()).generate("system", "continue");
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(token, idx)) >= 0) {
            count++;
            idx += token.length();
        }
        return count;
    }
}








