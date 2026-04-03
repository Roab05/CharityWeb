package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DisbursementResponse {
    private String disbursementId;
    private String projectId;
    private BigDecimal amount;
    private LocalDateTime disbursementTime;
    private String reason;
    private String evidenceURL;
    private String recipientInfo;
    private Integer status;
}