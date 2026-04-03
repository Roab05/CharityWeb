package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "project_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectActivity {

    @Id
    @UuidGenerator
    @Column(name = "activity_id")
    private String activityId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageURL;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}