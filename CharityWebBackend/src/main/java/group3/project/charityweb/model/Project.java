package group3.project.charityweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @UuidGenerator
    private String id;

    private String name;

    private String category;

    private String description;

    private Long fundRaised;

    private Long fundraisingGoal;
}
