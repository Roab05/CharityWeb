package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DonationResponse {
    private String donorName; // Trích xuất từ Individual/Organization
    private BigDecimal amount;
    private String message;
    private LocalDateTime donationTime;
}