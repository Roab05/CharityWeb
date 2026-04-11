package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "project_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCategory {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Project> projects;
}