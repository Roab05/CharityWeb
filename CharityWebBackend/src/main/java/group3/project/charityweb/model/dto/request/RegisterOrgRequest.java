package group3.project.charityweb.model.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterOrgRequest {
    private String username;
    private String password;
    private String name;
    private LocalDate doe;
    private String websiteURL;
    private String address;
    private String description;
    private String email;
    private String phone;
}
