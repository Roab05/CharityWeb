package group3.project.charityweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Donor {
    @Id
    @UuidGenerator
    private String id;

    private Boolean admin;

    @Column(unique = true)
    private String email;


    private String password;


    @Column(unique = true)
    private String phoneNumber;


    @Column(unique = true)
    private String displayName;

    private Long totalDonation;
}