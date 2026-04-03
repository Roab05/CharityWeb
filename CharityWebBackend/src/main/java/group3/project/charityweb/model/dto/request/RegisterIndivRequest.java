package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class RegisterIndivRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullName;
    private String address;
}