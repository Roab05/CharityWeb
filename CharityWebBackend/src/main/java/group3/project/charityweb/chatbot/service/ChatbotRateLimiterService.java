package group3.project.charityweb.chatbot.service;

import group3.project.charityweb.chatbot.config.ChatbotProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatbotRateLimiterService {

    private final Map<String, CounterWindow> counters = new ConcurrentHashMap<>();
    private final ChatbotProperties chatbotProperties;

    public ChatbotRateLimiterService(ChatbotProperties chatbotProperties) {
        this.chatbotProperties = chatbotProperties;
    }

    public boolean allow(String key, boolean authenticated) {
        int limit = authenticated
                ? chatbotProperties.getAuthenticatedRateLimit()
                : chatbotProperties.getAnonymousRateLimit();
        long now = Instant.now().getEpochSecond();
        long windowStart = now - chatbotProperties.getRateWindowSeconds();

        CounterWindow result = counters.compute(key, (k, existing) -> {
            if (existing == null || existing.startEpochSecond < windowStart) {
                return new CounterWindow(now, 1);
            }
            return new CounterWindow(existing.startEpochSecond, existing.count + 1);
        });

        return result.count <= limit;
    }

    private record CounterWindow(long startEpochSecond, int count) {
    }
}

