package group3.project.charityweb.model.dto.request;

import group3.project.charityweb.model.enums.ProjectStatus;
import lombok.Data;

@Data
public class UpdateProjectStatusRequest {
    private ProjectStatus status;
}
