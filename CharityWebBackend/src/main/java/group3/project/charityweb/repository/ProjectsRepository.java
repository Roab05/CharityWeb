package group3.project.charityweb.repository;

import group3.project.charityweb.model.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, String> {
    List<Projects> findAllByOrderByStateAscCreatedAtDesc();
}
