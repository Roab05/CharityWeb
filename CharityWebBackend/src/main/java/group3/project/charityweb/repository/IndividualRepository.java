package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, String> {
    boolean existsByPhone(String phone);
}