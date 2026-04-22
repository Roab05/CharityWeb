package group3.project.charityweb.chatbot.exception;

public class ChatbotRateLimitException extends RuntimeException {
    public ChatbotRateLimitException(String message) {
        super(message);
    }
}

