package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, String> {
    List<Donation> findByUser_IdOrderByDonationTimeDesc(String userId);
    List<Donation> findByProject_ProjectIdAndStatusOrderByDonationTimeDesc(String projectId, Integer status);
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 1")
    BigDecimal sumAllSuccessfulDonations();
}