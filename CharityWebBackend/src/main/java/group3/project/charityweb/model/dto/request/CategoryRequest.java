package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private String id; // VD: "EDU", "HEALTH". Chỉ dùng khi POST tạo mới
    private String categoryName;
    private String description;
}