package group3.project.charityweb.exception;

public class PaymentUrlGenerationException extends RuntimeException {
    public PaymentUrlGenerationException(String message) {
        super(message);
    }
    public PaymentUrlGenerationException(String message, Throwable cause) {}
}
