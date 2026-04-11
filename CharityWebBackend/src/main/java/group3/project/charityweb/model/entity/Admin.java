package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Kế thừa từ Account
public class Admin extends Account {

    @Column(name = "admin_level")
    private String adminLevel;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}