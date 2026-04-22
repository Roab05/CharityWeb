package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Disbursement;
import group3.project.charityweb.model.enums.DisbursementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, String> {

    List<Disbursement> findByProject_ProjectIdOrderByDisbursementTimeDesc(String projectId);
    Page<Disbursement> findByProject_ProjectIdOrderByDisbursementTimeDesc(String projectId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Disbursement d WHERE d.project.projectId = :projectId AND d.status = :status")
    BigDecimal sumDisbursedAmountByProjectId(@Param("projectId") String projectId, @Param("status") DisbursementStatus status);

    List<Disbursement> findAllByStatus(DisbursementStatus status);
    Page<Disbursement> findAllByStatus(DisbursementStatus status, Pageable pageable);
}