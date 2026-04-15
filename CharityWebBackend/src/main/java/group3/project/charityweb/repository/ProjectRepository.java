package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByStatusAndCategories_Id(ProjectStatus status, String categories_id, Pageable pageable);

    List<Project> findByStatusOrderByCreatedAtDesc(ProjectStatus status);

    Page<Project> findByStatusOrderByCreatedAtDesc(ProjectStatus status, Pageable pageable);

    long countByStatus(ProjectStatus status);
}