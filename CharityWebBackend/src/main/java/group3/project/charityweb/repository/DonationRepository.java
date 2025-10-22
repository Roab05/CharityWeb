package group3.project.charityweb.repository;

import group3.project.charityweb.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {

}
