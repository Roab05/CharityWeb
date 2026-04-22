package group3.project.charityweb.chatbot.client.impl;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.client.dto.GeminiRequest;
import group3.project.charityweb.chatbot.client.dto.GeminiResponse;
import group3.project.charityweb.chatbot.config.GeminiProperties;
import group3.project.charityweb.chatbot.exception.ChatbotUpstreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiHttpClient implements GeminiClient {

    private final RestClient geminiRestClient;
    private final GeminiProperties geminiProperties;

    @Override
    public String generate(String systemPrompt, String userPrompt) {
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

        String endpoint = "/v1beta/models/" + geminiProperties.getModel() + ":generateContent?key=" + apiKey;

        // Log endpoint nhưng che API Key đi để đảm bảo bảo mật
        String maskedEndpoint = "/v1beta/models/" + geminiProperties.getModel() + ":generateContent?key=***";
        log.info("[Gemini API] Đang gửi request tới: {}", maskedEndpoint);

        GeminiResponse response;
        try {
            response = geminiRestClient.post()
                    .uri(endpoint)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);
            log.info("[Gemini API] Request thành công.");
        } catch (RestClientResponseException ex) {
            // Xử lý các lỗi HTTP 4xx (Sai key, bad request, limit) và 5xx (Server Google lỗi)
            log.error("[Gemini API] Lỗi HTTP {} - Trạng thái: {}. Chi tiết response: {}",
                    ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
            throw new ChatbotUpstreamException("Lỗi từ Gemini API (HTTP " + ex.getStatusCode() + ").", ex);
        } catch (ResourceAccessException ex) {
            // Xử lý lỗi network, timeout, không phân giải được DNS
            log.error("[Gemini API] Lỗi kết nối hoặc Timeout khi gọi API.", ex);
            throw new ChatbotUpstreamException("Lỗi kết nối/timeout tới Gemini API.", ex);
        } catch (Exception ex) {
            // Catch-all cho các lỗi khác
            log.error("[Gemini API] Lỗi không xác định khi gọi API.", ex);
            throw new ChatbotUpstreamException("Không thể gọi Gemini API do lỗi hệ thống.", ex);
        }

        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            log.warn("[Gemini API] Response không có dữ liệu (candidates rỗng).");
            throw new ChatbotUpstreamException("Gemini trả về dữ liệu rỗng.");
        }

        GeminiResponse.Candidate firstCandidate = response.getCandidates().getFirst();
        if (firstCandidate.getContent() == null || firstCandidate.getContent().getParts() == null || firstCandidate.getContent().getParts().isEmpty()) {
            log.warn("[Gemini API] Response hợp lệ nhưng content parts bị rỗng.");
            throw new ChatbotUpstreamException("Gemini trả về response không hợp lệ.");
        }

        String text = firstCandidate.getContent().getParts().getFirst().getText();
        if (text == null || text.isBlank()) {
            log.warn("[Gemini API] AI trả về text rỗng.");
            throw new ChatbotUpstreamException("Gemini trả về câu trả lời rỗng.");
        }

        return text.trim();
    }
}