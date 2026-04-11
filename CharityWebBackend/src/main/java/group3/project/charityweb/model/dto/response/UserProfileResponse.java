package group3.project.charityweb.model.dto.response;

import group3.project.charityweb.model.enums.AccountStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserProfileResponse {
    private String accountId;
    private String username;
    private String roleType; // ADMIN, INDIVIDUAL, ORGANIZATION
    private AccountStatus status;

    // Thuộc tính chung của User
    private String email;
    private String phone;
    private BigDecimal totalDonatedAmount;

    // Dành cho Individual
    private String fullName;
    private String address;

    // Dành cho Organization
    private String orgName;
    private String websiteURL;
    private String description;
}