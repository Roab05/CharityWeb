package group3.project.charityweb.repository;

import group3.project.charityweb.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, String> {
    Optional<Donor> findByEmail(String email);

    Optional<Donor> findByPhoneNumber(String phoneNumber);

    Optional<Donor> findByDisplayName(String displayName);
}
