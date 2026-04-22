package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.ProjectActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectActivityRepository extends JpaRepository<ProjectActivity, String> {
    List<ProjectActivity> findByProject_ProjectId(String projectId);
    Page<ProjectActivity> findByProject_ProjectId(String projectId, Pageable pageable);
}