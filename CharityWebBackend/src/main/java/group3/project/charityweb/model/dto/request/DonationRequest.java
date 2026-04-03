package group3.project.charityweb.model.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DonationRequest {
    private BigDecimal amount;
    private String message;
}