package group3.project.charityweb.model.dto.request;

import group3.project.charityweb.model.enums.AccountStatus;
import lombok.Data;

@Data
public class UpdateAccountStatusRequest {
    private AccountStatus status;
}
