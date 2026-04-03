package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, String> {

    List<Disbursement> findByProject_ProjectIdOrderByDisbursementTimeDesc(String projectId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Disbursement d WHERE d.project.projectId = :projectId AND d.status = 1")
    BigDecimal sumDisbursedAmountByProjectId(@Param("projectId") String projectId);
}