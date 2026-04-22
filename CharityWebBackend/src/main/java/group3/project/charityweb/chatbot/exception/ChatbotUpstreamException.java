package group3.project.charityweb.chatbot.exception;

public class ChatbotUpstreamException extends RuntimeException {
    public ChatbotUpstreamException(String message) {
        super(message);
    }

    public ChatbotUpstreamException(String message, Throwable cause) {
        super(message, cause);
    }
}

