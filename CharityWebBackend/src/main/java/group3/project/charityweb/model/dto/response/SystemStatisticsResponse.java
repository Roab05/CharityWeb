package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SystemStatisticsResponse {
    private BigDecimal totalDonatedAmount;
    private long activeProjectsCount;
    private long pendingOrganizationsCount;
    private long pendingProjectsCount;
}