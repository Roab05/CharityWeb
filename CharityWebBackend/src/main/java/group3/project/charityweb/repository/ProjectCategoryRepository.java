package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, String> {

}