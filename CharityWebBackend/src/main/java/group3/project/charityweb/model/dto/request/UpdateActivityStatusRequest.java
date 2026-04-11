package group3.project.charityweb.model.dto.request;

import group3.project.charityweb.model.enums.ActivityStatus;
import lombok.Data;

@Data
public class UpdateActivityStatusRequest {
    private ActivityStatus status;
}