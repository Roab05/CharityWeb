package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String phone;
    private String address;
    private String fullName;
    private String name;
    private String websiteURL;
    private String description;
    private String contactEmail;
    private String contactPhone;
}