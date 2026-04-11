package group3.project.charityweb.model.dto.request;

import group3.project.charityweb.model.enums.DisbursementStatus;
import lombok.Data;

@Data
public class UpdateDisbursementStatusRequest {
    private DisbursementStatus status;
}
