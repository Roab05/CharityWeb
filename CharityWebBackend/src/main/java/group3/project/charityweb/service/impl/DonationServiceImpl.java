package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.DonationRequest;
import group3.project.charityweb.model.dto.response.DonationResponse;
import group3.project.charityweb.model.dto.response.PaymentUrlResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.model.enums.DonationStatus;
import group3.project.charityweb.model.enums.ProjectStatus;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.DonationRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.repository.TransactionRepository;
import group3.project.charityweb.service.DonationService;
import group3.project.charityweb.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;
    private final DonationRepository donationRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public PaymentUrlResponse initiateDonation(String username, String projectId, DonationRequest request, HttpServletRequest httpRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));

        if (project.getStatus() != ProjectStatus.ACTIVE) {
            throw new RuntimeException("Dự án hiện không nhận quyên góp!");
        }

        User user = (User) accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không hợp lệ"));

        Donation donation = new Donation();
        donation.setProject(project);
        donation.setUser(user);
        donation.setAmount(request.getAmount());
        donation.setMessage(request.getMessage());
        donation.setDonationTime(LocalDateTime.now());
        donation.setStatus(DonationStatus.PROCESSING);
        donation = donationRepository.save(donation);

        Transaction transaction = new Transaction();
        transaction.setDonation(donation);
        transaction.setAmount(request.getAmount());
        transaction.setGatewayName("VNPAY");
        transaction.setPaymentStatus(2);
        transaction = transactionRepository.save(transaction);

        String url = paymentService.createPaymentUrl(transaction, httpRequest);
        return new PaymentUrlResponse(url);
    }

    @Override
    public Page<DonationResponse> getProjectDonations(String projectId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Donation> donations = donationRepository.findByProject_ProjectIdAndStatusOrderByDonationTimeDesc(
                projectId,
                DonationStatus.SUCCESS,
                pageRequest
        );

        return donations.map(d -> {
            String donorName = "Nhà hảo tâm ẩn danh";
            if (d.getUser() instanceof Individual ind) donorName = ind.getFullName();
            else if (d.getUser() instanceof Organization org) donorName = org.getName();

            return DonationResponse.builder()
                    .donorName(donorName)
                    .amount(d.getAmount())
                    .message(d.getMessage())
                    .donationTime(d.getDonationTime())
                    .build();
        });
    }
}