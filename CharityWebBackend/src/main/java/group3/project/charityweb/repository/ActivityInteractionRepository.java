package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.ActivityInteraction;
import group3.project.charityweb.model.enums.InteractionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityInteractionRepository extends JpaRepository<ActivityInteraction, String> {
    List<ActivityInteraction> findByActivity_ActivityIdOrderByCreatedAtDesc(String activityId);
    Page<ActivityInteraction> findByActivity_ActivityIdOrderByCreatedAtDesc(String activityId, Pageable pageable);
    Optional<ActivityInteraction> findFirstByActivity_ActivityIdAndUser_IdAndType(
            String activityId,
            String userId,
            InteractionType type
    );

    Optional<ActivityInteraction> findFirstByActivity_ActivityIdAndUser_UsernameAndType(
            String activityId,
            String username,
            InteractionType type
    );

    Long deleteByActivity_ActivityIdAndUser_IdAndType(
            String activityId,
            String userId,
            InteractionType type
    );

    Long deleteByActivity_ActivityIdAndUser_IdAndTypeIn(
            String activityId,
            String userId,
            java.util.Collection<InteractionType> types
    );

    Long deleteByActivity_ActivityId(String activityId);
}