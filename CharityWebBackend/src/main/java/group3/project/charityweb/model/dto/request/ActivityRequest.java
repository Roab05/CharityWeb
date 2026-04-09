package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class ActivityRequest {
    private String title;
    private String content;
    private String imageURL;
}