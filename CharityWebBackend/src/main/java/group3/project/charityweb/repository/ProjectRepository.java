package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Page<Project> findByStatus(Integer status, Pageable pageable);
    Page<Project> findByStatusAndCategories_Id(Integer status, String categoryId, Pageable pageable);
    List<Project> findByStatusOrderByCreatedAtDesc(Integer status);
    long countByStatus(Integer status);
}