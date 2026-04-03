package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Organization extends User {

    @Column(name = "name")
    private String name;

    @Column(name = "doe") // Date of Establishment
    private LocalDate doe;

    @Column(name = "website_url")
    private String websiteURL;

    @Column(name = "address")
    private String address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone", length = 15)
    private String contactPhone;

    // Mapping bảng trung gian Organization_Project theo đúng sơ đồ của bạn
    @ManyToMany(mappedBy = "organizations")
    private List<Project> projects;
}