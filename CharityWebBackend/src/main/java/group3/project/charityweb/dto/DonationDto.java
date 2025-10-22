package group3.project.charityweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DonationDto {
    private String name;
    private String email;
    private String phone;
    private Long amount;
    private String message;
}
