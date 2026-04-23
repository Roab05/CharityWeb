package group3.project.charityweb.chatbot.client.impl;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.client.dto.*;
import group3.project.charityweb.chatbot.config.GeminiProperties;
import group3.project.charityweb.chatbot.exception.ChatbotUpstreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiHttpClient implements GeminiClient {

    private static final int HTTP_SERVICE_UNAVAILABLE = 503;

    private final RestClient geminiRestClient;
    private final GeminiProperties geminiProperties;

    @Override
    public GeminiGenerationResult generate(String systemPrompt, String userPrompt) {
        String apiKey = geminiProperties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.error("[Gemini API] Lỗi cấu hình: API Key bị trống hoặc null.");
            throw new ChatbotUpstreamException("Gemini API key chưa được cấu hình.");
        }

        GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(
                        GeminiRequest.Content.builder()
                                .role("user")
                                .parts(List.of(
                                        GeminiRequest.Part.builder().text(systemPrompt).build(),
                                        GeminiRequest.Part.builder().text(userPrompt).build()
                                ))
                                .build()
                ))
                .generationConfig(GeminiRequest.GenerationConfig.builder()
                        .temperature(geminiProperties.getTemperature())
                        .maxOutputTokens(geminiProperties.getMaxOutputTokens())
                        .build())
                .build();

        List<String> models = buildModelCandidates();
        int maxAttempts = Math.max(1, geminiProperties.getRetryMaxAttempts());
        ChatbotUpstreamException lastFailure = null;

        for (String model : models) {
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    GeminiResponse response = callGemini(model, apiKey, request);
                    return extractGenerationResult(response);
                } catch (RestClientResponseException ex) {
                    int statusCode = ex.getStatusCode().value();
                    boolean retryable = statusCode == HTTP_SERVICE_UNAVAILABLE;
                    if (retryable && shouldRetry(attempt, maxAttempts)) {
                        sleepBackoff(attempt, model, "HTTP " + statusCode);
                        continue;
                    }

                    log.error("[Gemini API] Lỗi HTTP {} - Trạng thái: {}. Chi tiết response: {}",
                            ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
                    lastFailure = new ChatbotUpstreamException("Lỗi từ Gemini API (HTTP " + ex.getStatusCode() + ").", ex);

                    if (statusCode == HTTP_SERVICE_UNAVAILABLE) {
                        log.warn("[Gemini API] Model {} quá tải, thử model khác nếu có.", model);
                        break;
                    }
                    throw lastFailure;
                } catch (ResourceAccessException ex) {
                    if (shouldRetry(attempt, maxAttempts)) {
                        sleepBackoff(attempt, model, "NETWORK/TIMEOUT");
                        continue;
                    }
                    log.error("[Gemini API] Lỗi kết nối hoặc Timeout khi gọi API.", ex);
                    lastFailure = new ChatbotUpstreamException("Lỗi kết nối/timeout tới Gemini API.", ex);
                    break;
                } catch (RestClientException ex) {
                    boolean timeoutLike = isTimeoutLike(ex);
                    if (timeoutLike && shouldRetry(attempt, maxAttempts)) {
                        sleepBackoff(attempt, model, "READ_TIMEOUT");
                        continue;
                    }
                    log.error("[Gemini API] Lỗi client khi đọc response từ model {}.", model, ex);
                    lastFailure = new ChatbotUpstreamException("Lỗi đọc response từ Gemini API.", ex);
                    break;
                } catch (ChatbotUpstreamException ex) {
                    throw ex;
                } catch (Exception ex) {
                    log.error("[Gemini API] Lỗi không xác định khi gọi API.", ex);
                    throw new ChatbotUpstreamException("Không thể gọi Gemini API do lỗi hệ thống.", ex);
                }
            }
        }

        if (lastFailure != null) {
            throw lastFailure;
        }
        throw new ChatbotUpstreamException("Không thể gọi Gemini API do lỗi hệ thống.");
    }

    private GeminiResponse callGemini(String model, String apiKey, GeminiRequest request) {
        String endpoint = "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        String maskedEndpoint = "/v1beta/models/" + model + ":generateContent?key=***";
        log.info("[Gemini API] Đang gửi request tới: {}", maskedEndpoint);

        GeminiResponse response = geminiRestClient.post()
                .uri(endpoint)
                .header("Accept", "application/json")
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);
        log.info("[Gemini API] Request thành công với model {}.", model);
        return response;
    }

    private GeminiGenerationResult extractGenerationResult(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            log.warn("[Gemini API] Response không có dữ liệu (candidates rỗng).");
            throw new ChatbotUpstreamException("Gemini trả về dữ liệu rỗng.");
        }

        if (response.getUsageMetadata() != null) {
            log.info("[Gemini API] Token usage - prompt: {}, candidates: {}, total: {}",
                    response.getUsageMetadata().getPromptTokenCount(),
                    response.getUsageMetadata().getCandidatesTokenCount(),
                    response.getUsageMetadata().getTotalTokenCount());
        }

        GeminiResponse.Candidate firstCandidate = response.getCandidates().getFirst();
        log.info("[Gemini API] finishReason: {}", firstCandidate.getFinishReason());
        if (firstCandidate.getContent() == null || firstCandidate.getContent().getParts() == null || firstCandidate.getContent().getParts().isEmpty()) {
            log.warn("[Gemini API] Response hợp lệ nhưng content parts bị rỗng.");
            throw new ChatbotUpstreamException("Gemini trả về response không hợp lệ.");
        }

        String text = firstCandidate.getContent().getParts().stream()
                .map(GeminiResponse.Part::getText)
                .filter(partText -> partText != null && !partText.isBlank())
                .map(String::trim)
                .collect(Collectors.joining("\n"));
        if (text.isBlank()) {
            log.warn("[Gemini API] AI trả về text rỗng.");
            throw new ChatbotUpstreamException("Gemini trả về câu trả lời rỗng.");
        }

        return GeminiGenerationResult.builder()
                .text(text.trim())
                .finishReason(firstCandidate.getFinishReason())
                .build();
    }

    @Override
    public List<Float> getEmbedding(String text) {
        String apiKey = geminiProperties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.error("[Gemini API] Lỗi cấu hình: API Key bị trống.");
            throw new ChatbotUpstreamException("Gemini API key chưa được cấu hình.");
        }

        GeminiEmbeddingRequest request = GeminiEmbeddingRequest.builder()
                .model("models/gemini-embedding-2")
                .content(GeminiEmbeddingRequest.Content.builder()
                        .parts(List.of(GeminiEmbeddingRequest.Part.builder()
                                .text(text)
                                .build()))
                        .build())
                .build();

        try {
            String endpoint = "/v1beta/models/gemini-embedding-2:embedContent?key=" + apiKey;
            log.info("[Gemini API] Đang gửi request Embedding...");

            GeminiEmbeddingResponse response = geminiRestClient.post()
                    .uri(endpoint)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        log.error("[Gemini API] Lỗi khi gọi Embedding: {}", res.getStatusCode());
                        throw new ChatbotUpstreamException("Không thể lấy Vector từ Gemini.");
                    })
                    .body(GeminiEmbeddingResponse.class);

            if (response != null && response.getEmbedding() != null) {
                List<Float> values = response.getEmbedding().getValues();
                log.info("[Gemini API] Lấy Vector thành công (Size: {})", values.size());
                return values;
            }

            throw new ChatbotUpstreamException("Dữ liệu Vector trả về bị rỗng.");

        } catch (Exception ex) {
            log.error("[Gemini API] Lỗi hệ thống khi gọi Embedding: {}", ex.getMessage());
            throw new ChatbotUpstreamException("Lỗi kết nối Gemini API để lấy Embedding.");
        }
    }

    private boolean shouldRetry(int attempt, int maxAttempts) {
        return geminiProperties.isRetryEnabled() && attempt < maxAttempts;
    }

    private void sleepBackoff(int attempt, String model, String reason) {
        long base = Math.max(50L, geminiProperties.getRetryInitialBackoffMs());
        long backoff = base * (1L << Math.max(0, attempt - 1));
        log.warn("[Gemini API] Retry model {} sau {}ms vi {} (attempt {}).", model, backoff, reason, attempt + 1);
        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ChatbotUpstreamException("Retry Gemini API bị gián đoạn.", ex);
        }
    }

    private List<String> buildModelCandidates() {
        Set<String> ordered = new LinkedHashSet<>();
        addModel(ordered, geminiProperties.getModel());
        addModel(ordered, geminiProperties.getFallbackModel());

        String fallbackModels = geminiProperties.getFallbackModels();
        if (fallbackModels != null && !fallbackModels.isBlank()) {
            for (String model : fallbackModels.split(",")) {
                addModel(ordered, model);
            }
        }
        return new ArrayList<>(ordered);
    }

    private void addModel(Set<String> models, String model) {
        if (model == null) {
            return;
        }
        String normalized = model.trim();
        if (!normalized.isBlank()) {
            models.add(normalized);
        }
    }

    private boolean isTimeoutLike(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            String message = current.getMessage();
            if (message != null && message.toLowerCase().contains("timed out")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}