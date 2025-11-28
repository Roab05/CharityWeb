package group3.project.charityweb.repository;

import group3.project.charityweb.model.Donations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationsRepository extends JpaRepository<Donations,Long> {

    List<Donations> findAllByUserIdOrderByDateTimeDesc(String userId);

    List<Donations> findAllByProjectIdOrderByDateTimeDesc(String projectId);
}
