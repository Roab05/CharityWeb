package group3.project.charityweb.repository;

import group3.project.charityweb.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @EntityGraph(attributePaths = {"donation", "donation.project"})
    Page<Transaction> findByDonation_User_IdOrderByCompletedAtDesc(String userId, Pageable pageable);
}