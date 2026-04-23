package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectCategoryResponse {
    private String id;
    private String categoryName;
    private String description;
}