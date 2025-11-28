package group3.project.charityweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @UuidGenerator
    private String id;

    private Boolean admin;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true, columnDefinition = "NVARCHAR(255)")
    private String displayName;
}