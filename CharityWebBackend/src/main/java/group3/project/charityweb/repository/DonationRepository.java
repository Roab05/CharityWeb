package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Donation;
import group3.project.charityweb.model.enums.DonationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, String> {
    List<Donation> findByUser_IdOrderByDonationTimeDesc(String userId);
    Page<Donation> findByUser_IdOrderByDonationTimeDesc(String userId, Pageable pageable);

    List<Donation> findByProject_ProjectIdAndStatusOrderByDonationTimeDesc(String project_projectId, DonationStatus status);
    Page<Donation> findByProject_ProjectIdAndStatusOrderByDonationTimeDesc(String project_projectId, DonationStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = :status")
    BigDecimal sumAllSuccessfulDonations(@Param("status") DonationStatus status);
}