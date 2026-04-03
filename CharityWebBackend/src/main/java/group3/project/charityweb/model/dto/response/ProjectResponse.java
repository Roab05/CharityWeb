package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectResponse {
    private String projectId;
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Integer status;
    private String backgroundImageURL;
    private String bankAccountNo;

    private List<String> categories;
    private List<String> organizationNames;
}