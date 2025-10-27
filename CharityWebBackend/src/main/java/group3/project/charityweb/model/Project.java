package group3.project.charityweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @UuidGenerator
    private String id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String category;

    @Column(columnDefinition = "NVARCHAR(4000)")
    private String description;

    private Long currentAmount;

    private Long targetAmount;

    private Long donationCount;

    private LocalDate startDate;

    private LocalDate endDate;
}


