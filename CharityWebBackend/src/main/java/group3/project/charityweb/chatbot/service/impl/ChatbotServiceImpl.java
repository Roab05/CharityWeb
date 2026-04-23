package group3.project.charityweb.chatbot.service.impl;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.client.dto.GeminiGenerationResult;
import group3.project.charityweb.chatbot.config.ChatbotProperties;
import group3.project.charityweb.chatbot.config.GeminiProperties;
import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatAskResponse;
import group3.project.charityweb.chatbot.dto.response.ChatMeta;
import group3.project.charityweb.chatbot.dto.response.ChatSource;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private static final Pattern TRAILING_LIST_MARKER = Pattern.compile("(?s).*(\\n\\s*(?:[*-]|\\d+[.)]?))$");
    private static final String FINISH_REASON_MAX_TOKENS = "MAX_TOKENS";
    private static final int MAX_CONTINUATION_ATTEMPTS = 3;

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

        Instant contextStart = Instant.now();
        ChatContext context = contextBuilderService.build(request.getProjectId(), request.getQuestion());
        String systemPrompt = promptTemplateService.buildSystemPrompt(request.getLocale());
        String userPrompt = promptTemplateService.buildUserPrompt(request.getQuestion().trim(), context.getText());
        long contextBuildLatency = Duration.between(contextStart, Instant.now()).toMillis();

        String answer;
        boolean fallbackUsed = false;
        String safetyNotice = null;
        long modelCallLatency;
        Instant modelStart = Instant.now();

        try {
            GeminiGenerationResult firstResult = geminiClient.generate(systemPrompt, userPrompt);
            answer = firstResult.getText();
            String finishReason = firstResult.getFinishReason();

            for (int i = 0; i < MAX_CONTINUATION_ATTEMPTS; i++) {
                if (!shouldContinue(answer, finishReason)) {
                    break;
                }

                String continuationPrompt = promptTemplateService.buildContinuationPrompt(
                        request.getQuestion().trim(),
                        context.getText(),
                        answer
                );
                try {
                    GeminiGenerationResult continuationResult = geminiClient.generate(systemPrompt, continuationPrompt);
                    answer = mergeAnswer(answer, continuationResult.getText());
                    finishReason = continuationResult.getFinishReason();
                } catch (ChatbotUpstreamException ignored) {
                    break;
                }
            }

            modelCallLatency = Duration.between(modelStart, Instant.now()).toMillis();
        } catch (ChatbotUpstreamException ex) {
            modelCallLatency = Duration.between(modelStart, Instant.now()).toMillis();
            if (chatbotProperties.isStrictUpstream()) {
                throw ex;
            }
            fallbackUsed = true;
            safetyNotice = "AI tam thoi khong san sang, cau tra loi duoi day la thong diep huong dan mac dinh.";
            answer = buildContextualFallbackAnswer(request, context);
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
                        .contextBuildLatencyMs(contextBuildLatency)
                        .modelCallLatencyMs(modelCallLatency)
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

    private boolean looksIncomplete(String answer) {
        if (answer == null || answer.isBlank()) {
            return true;
        }
        String trimmed = answer.trim();
        if (trimmed.length() < 24) {
            return true;
        }
        if (trimmed.endsWith("*") || trimmed.endsWith("-") || trimmed.endsWith(":")) {
            return true;
        }
        if (TRAILING_LIST_MARKER.matcher(trimmed).matches()) {
            return true;
        }
        if (trimmed.endsWith("\n1") || trimmed.endsWith("\n1.") || trimmed.endsWith("\n1)")) {
            return true;
        }

        String[] lines = trimmed.split("\\n");
        String lastLine = lines[lines.length - 1].trim();
        if (lastLine.startsWith("*") || lastLine.startsWith("-") || lastLine.matches("^\\d+[.)].*")) {
            // Bullet/list line ending without sentence punctuation is often a truncated answer.
            if (!lastLine.endsWith(".") && !lastLine.endsWith("!") && !lastLine.endsWith("?") && !lastLine.endsWith("`") && !lastLine.endsWith("\"")) {
                return true;
            }
        }

        return hasUnbalancedMarkdownBold(trimmed);
    }

    private String mergeAnswer(String first, String continuation) {
        if (continuation == null || continuation.isBlank()) {
            return first;
        }
        String firstTrimmed = first == null ? "" : first.trim();
        String continuationTrimmed = continuation.trim();

        if (firstTrimmed.isBlank()) {
            return deduplicateAnswer(continuationTrimmed);
        }

        if (continuationTrimmed.startsWith(firstTrimmed) || continuationTrimmed.contains(firstTrimmed)) {
            return deduplicateAnswer(continuationTrimmed);
        }
        if (firstTrimmed.contains(continuationTrimmed)) {
            return deduplicateAnswer(firstTrimmed);
        }

        int overlap = computeSuffixPrefixOverlap(firstTrimmed, continuationTrimmed);
        String merged = overlap > 0
                ? firstTrimmed + continuationTrimmed.substring(overlap)
                : firstTrimmed + "\n" + continuationTrimmed;
        return deduplicateAnswer(merged);
    }

    private boolean shouldContinue(String answer, String finishReason) {
        if (FINISH_REASON_MAX_TOKENS.equalsIgnoreCase(finishReason)) {
            return true;
        }
        return looksIncomplete(answer);
    }

    private int computeSuffixPrefixOverlap(String first, String second) {
        int maxOverlap = Math.min(first.length(), second.length());
        for (int i = maxOverlap; i >= 20; i--) {
            if (first.regionMatches(first.length() - i, second, 0, i)) {
                return i;
            }
        }
        return 0;
    }

    private String deduplicateAnswer(String answer) {
        String[] blocks = answer.split("\\n\\s*\\n");
        Set<String> seenLongBlocks = new HashSet<>();
        List<String> uniqueBlocks = new ArrayList<>();

        for (String block : blocks) {
            String compact = normalize(block);
            if (compact.isBlank()) {
                continue;
            }
            // Keep short blocks even if duplicated; remove repeated long narrative/list blocks.
            if (compact.length() > 40 && !seenLongBlocks.add(compact)) {
                continue;
            }
            uniqueBlocks.add(removeConsecutiveDuplicateLines(block).trim());
        }

        return String.join("\n\n", uniqueBlocks).trim();
    }

    private String removeConsecutiveDuplicateLines(String block) {
        String[] lines = block.split("\\n");
        List<String> result = new ArrayList<>();
        String previous = null;

        for (String line : lines) {
            String normalized = normalize(line);
            if (!normalized.isBlank() && normalized.equals(previous)) {
                continue;
            }
            result.add(line);
            previous = normalized;
        }
        return String.join("\n", result);
    }

    private String normalize(String input) {
        return input == null ? "" : input.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    private boolean hasUnbalancedMarkdownBold(String text) {
        int count = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == '*' && text.charAt(i + 1) == '*') {
                count++;
                i++;
            }
        }
        return count % 2 != 0;
    }

    private String buildContextualFallbackAnswer(ChatAskRequest request, ChatContext context) {
        String question = request != null && request.getQuestion() != null ? request.getQuestion().toLowerCase() : "";
        String projectId = request != null ? request.getProjectId() : null;
        boolean donationIntent = question.contains("donate") || question.contains("quyen gop") || question.contains("ung ho") || question.contains("donation");
        boolean historyIntent = question.contains("lich su") || question.contains("xem lai") || question.contains("da ung ho") || question.contains("luot ung ho");
        boolean transactionIntent = question.contains("giao dich") || question.contains("transaction") || question.contains("thanh toan");

        String projectTitle = null;
        if (context != null && context.getSources() != null) {
            for (ChatSource source : context.getSources()) {
                if ("PROJECT".equalsIgnoreCase(source.getType())) {
                    projectTitle = source.getTitle();
                    break;
                }
            }
        }
        if (projectTitle == null || projectTitle.isBlank()) {
            projectTitle = "du an";
        }

        if (historyIntent && donationIntent) {
            return "Ban co the xem lich su cac luot ung ho tai users/me/donations. " +
                    "Neu can doi soat chi tiet giao dich thanh toan, hay xem them users/me/transactions.";
        }

        if (historyIntent || transactionIntent) {
            return "Ban co the xem lich su cac luot ung ho tai users/me/donations. " +
                    "Neu can doi soat chi tiet giao dich thanh toan, hay xem them users/me/transactions.";
        }

        if (donationIntent) {
            String effectiveProject = (projectId != null && !projectId.isBlank()) ? projectId : "{projectId}";
            return "De quyen gop cho \"" + projectTitle + "\", ban co the lam theo cac buoc:\n" +
                    "1) Xem chi tiet du an tai projects/" + effectiveProject + "\n" +
                    "2) Tao donation tai projects/" + effectiveProject + "/donations\n" +
                    "3) Hoan tat thanh toan VNPay theo URL he thong tra ve\n" +
                    "4) Kiem tra lich su luot ung ho tai users/me/donations";
        }

        return "Ban co the xem danh sach du an tai projects, " +
                "xem chi tiet du an tai projects/{projectId}, " +
                "va tao donation tai projects/{projectId}/donations.";
    }
}











