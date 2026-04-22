package group3.project.charityweb.model.entity;

import group3.project.charityweb.model.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @UuidGenerator
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Sử dụng BigDecimal thay vì Integer cho tiền tệ
    @Column(name = "target_amount", precision = 19, scale = 4)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", precision = 19, scale = 4)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status;

    @Column(name = "background_image_url")
    private String backgroundImageURL;

    @Column(name = "bank_account_no", length = 100)
    private String bankAccountNo;

    // Quan hệ nhiều-nhiều với Category
    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(
            name = "project_category_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<ProjectCategory> categories;

    // Quan hệ nhiều-nhiều với Organization (bảng trung gian Organization_Project)
    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(
            name = "organization_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private List<Organization> organizations;

    @OneToMany(mappedBy = "project")
    private List<ProjectActivity> activities;

    @OneToMany(mappedBy = "project")
    private List<Disbursement> disbursements;

    @OneToMany(mappedBy = "project")
    private List<Donation> donations;
}