package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, String> {
    Optional<Organization> findByUsername(String username);
    List<Organization> findByStatusOrderByCreatedAtDesc(Integer status);
    long countByStatus(Integer status);
    boolean existsByPhone(String phone);
}
