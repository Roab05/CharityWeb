package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "donation_id")
    private String donationId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "donation_time")
    private LocalDateTime donationTime;

    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id") // Trỏ về User chung
    private User user;

    @OneToOne(mappedBy = "donation", cascade = CascadeType.ALL)
    private Transaction transaction;
}