package group3.project.charityweb.model.dto.response;

import group3.project.charityweb.model.enums.DonationStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DonationHistoryResponse {
    private String donationId;
    private String projectId;
    private String projectName;
    private BigDecimal amount;
    private LocalDateTime donationTime;
    private String message;
    private DonationStatus status;
}