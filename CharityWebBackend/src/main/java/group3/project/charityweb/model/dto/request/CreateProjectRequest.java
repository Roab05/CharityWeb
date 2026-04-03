package group3.project.charityweb.model.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateProjectRequest {
    private String description;
    private BigDecimal targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String backgroundImageURL;
    private String bankAccountNo;
    private List<String> categoryIds;
}