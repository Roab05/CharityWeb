package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationSelectorResponse {
    private String organizationId;
    private String name;
}

