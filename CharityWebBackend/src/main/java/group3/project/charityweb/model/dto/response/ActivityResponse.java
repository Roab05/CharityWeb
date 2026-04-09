package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityResponse {
    private String activityId;
    private String title;
    private String content;
    private String imageURL;
}