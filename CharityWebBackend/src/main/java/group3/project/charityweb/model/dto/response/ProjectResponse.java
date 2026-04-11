package group3.project.charityweb.model.dto.response;

import group3.project.charityweb.model.enums.ProjectStatus;
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
    private String projectName;
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private ProjectStatus status;
    private String backgroundImageURL;
    private String bankAccountNo;

    private List<String> categories;
    private List<String> organizationNames;
}