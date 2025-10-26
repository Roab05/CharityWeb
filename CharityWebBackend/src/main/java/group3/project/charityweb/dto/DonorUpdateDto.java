package group3.project.charityweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DonorUpdateDto {
    private String id;
    private String displayName;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
    private Long amount;
}
