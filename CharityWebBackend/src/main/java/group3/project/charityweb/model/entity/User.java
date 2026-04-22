package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends Account {

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    // Đã đổi kiểu dữ liệu tiền thành BigDecimal
    @Column(name = "total_donated_amount", precision = 19, scale = 4)
    private BigDecimal totalDonatedAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "user")
    private List<Donation> donations;
}