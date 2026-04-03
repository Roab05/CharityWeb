package group3.project.charityweb.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_interaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String interactionId;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private Integer type;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ProjectActivity activity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
