package group3.project.charityweb.repository;

import group3.project.charityweb.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findAllByOrderByCreatedAtDesc();

    List<Project> findAllByOrderByCreatedAtAsc();
}
