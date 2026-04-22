package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_name")
    private String gatewayName; // VNPay, Momo...

    @Column(name = "gateway_transaction_no")
    private String gatewayTransactionNo;

    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "payment_status")
    private Integer paymentStatus;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;
}