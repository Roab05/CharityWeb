package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "disbursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "disbursement_id")
    private String disbursementId;

    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "disbursement_time")
    private LocalDateTime disbursementTime;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "evidence_url")
    private String evidenceURL;

    @Column(name = "status")
    private Integer status;

    @Column(name = "recipient_info")
    private String recipientInfo;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}