package group3.project.charityweb.chatbot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({GeminiProperties.class, ChatbotProperties.class})
public class ChatbotConfig {

    @Bean
    public RestClient geminiRestClient(RestClient.Builder builder, GeminiProperties geminiProperties, ChatbotProperties chatbotProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(chatbotProperties.getTimeoutMs());
        factory.setReadTimeout(chatbotProperties.getTimeoutMs());

        return builder
                .baseUrl(geminiProperties.getBaseUrl())
                .requestFactory(factory)
                .build();
    }
}

