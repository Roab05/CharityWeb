package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.ActivityInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityInteractionRepository extends JpaRepository<ActivityInteraction, String> {
    List<ActivityInteraction> findByActivity_ActivityIdOrderByCreatedAtDesc(String activityId);
}