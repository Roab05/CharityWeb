package group3.project.charityweb.model.dto.response;

import group3.project.charityweb.model.enums.DonationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionHistoryResponse {
    private String transactionId;
    private String donationId;
    private String projectId;
    private String projectName;
    private String gatewayName;
    private String gatewayTransactionNo;
    private BigDecimal amount;
    private Integer paymentStatus;
    private LocalDateTime completedAt;
    private LocalDateTime donationTime;
    private DonationStatus donationStatus;
}

