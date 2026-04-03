package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "individuals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Individual extends User {

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "address")
    private String address;
}