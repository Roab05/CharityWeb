package group3.project.charityweb.service;

import group3.project.charityweb.model.entity.Donation;
import group3.project.charityweb.model.entity.Transaction;
import group3.project.charityweb.model.enums.DonationStatus;
import group3.project.charityweb.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationStatusMaintenanceService {

    private static final int PAYMENT_STATUS_FAILED = 0;
    private static final int PAYMENT_STATUS_PROCESSING = 2;

    private final DonationRepository donationRepository;

    @Value("${payment.processing-timeout-seconds:30}")
    private long processingTimeoutSeconds;

    @Transactional
    public int expireProcessingDonations() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(processingTimeoutSeconds);
        List<Donation> staleDonations = donationRepository.findByStatusAndDonationTimeBeforeWithTransaction(
                DonationStatus.PROCESSING,
                cutoff
        );

        return markAsFailed(staleDonations);
    }

    @Transactional
    public int expireProcessingDonationsForUser(String userId) {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(processingTimeoutSeconds);
        List<Donation> staleDonations = donationRepository.findByUserIdAndStatusAndDonationTimeBeforeWithTransaction(
                userId,
                DonationStatus.PROCESSING,
                cutoff
        );

        return markAsFailed(staleDonations);
    }

    @Scheduled(fixedDelayString = "${payment.processing-check-interval-ms:15000}")
    public void scheduledExpireProcessingDonations() {
        expireProcessingDonations();
    }

    private int markAsFailed(List<Donation> staleDonations) {
        if (staleDonations.isEmpty()) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        for (Donation donation : staleDonations) {
            donation.setStatus(DonationStatus.FAILED);
            Transaction transaction = donation.getTransaction();
            if (transaction != null && (transaction.getPaymentStatus() == null || transaction.getPaymentStatus() == PAYMENT_STATUS_PROCESSING)) {
                transaction.setPaymentStatus(PAYMENT_STATUS_FAILED);
                if (transaction.getCompletedAt() == null) {
                    transaction.setCompletedAt(now);
                }
            }
        }

        donationRepository.saveAll(staleDonations);
        return staleDonations.size();
    }
}
