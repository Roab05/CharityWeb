package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private String id;
    private String categoryName;
    private String description;
}