package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.DonationRequest;
import group3.project.charityweb.model.dto.response.DonationResponse;
import group3.project.charityweb.model.dto.response.PaymentUrlResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.DonationRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.repository.TransactionRepository;
import group3.project.charityweb.service.DonationService;
import group3.project.charityweb.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;
    private final DonationRepository donationRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentService paymentService; // Gọi sang service thanh toán

    @Override
    @Transactional
    public PaymentUrlResponse initiateDonation(String username, String projectId, DonationRequest request, HttpServletRequest httpRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));

        if (project.getStatus() != 1) {
            throw new RuntimeException("Dự án hiện không nhận quyên góp!");
        }

        User user = (User) accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không hợp lệ"));

        // 1. Lưu Donation trạng thái Pending (2)
        Donation donation = new Donation();
        donation.setDonationId(UUID.randomUUID().toString());
        donation.setProject(project);
        donation.setUser(user);
        donation.setAmount(request.getAmount());
        donation.setMessage(request.getMessage());
        donation.setDonationTime(LocalDateTime.now());
        donation.setStatus(2); // 2: Pending
        donation = donationRepository.save(donation);

        // 2. Lưu Transaction trạng thái Pending (2)
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setDonation(donation);
        transaction.setAmount(request.getAmount());
        transaction.setGatewayName("VNPAY");
        transaction.setPaymentStatus(2); // 2: Pending
        transaction = transactionRepository.save(transaction);

        // 3. Gọi VNPAY tạo URL
        String url = paymentService.createPaymentUrl(transaction, httpRequest);
        return new PaymentUrlResponse(url);
    }

    @Override
    public List<DonationResponse> getProjectDonations(String projectId) {
        // Chỉ lấy trạng thái 1 (Thành công)
        List<Donation> donations = donationRepository.findByProject_ProjectIdAndStatusOrderByDonationTimeDesc(projectId, 1);

        return donations.stream().map(d -> {
            String donorName = "Nhà hảo tâm ẩn danh";
            if (d.getUser() instanceof Individual ind) donorName = ind.getFullName();
            else if (d.getUser() instanceof Organization org) donorName = org.getName();

            return DonationResponse.builder()
                    .donorName(donorName)
                    .amount(d.getAmount())
                    .message(d.getMessage())
                    .donationTime(d.getDonationTime())
                    .build();
        }).collect(Collectors.toList());
    }
}