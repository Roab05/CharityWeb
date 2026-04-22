package group3.project.charityweb.service.impl;

import group3.project.charityweb.model.dto.request.ChangePasswordRequest;
import group3.project.charityweb.service.UserService;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.UpdateProfileRequest;
import group3.project.charityweb.model.dto.response.DonationHistoryResponse;
import group3.project.charityweb.model.dto.response.TransactionHistoryResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.DonationRepository;
import group3.project.charityweb.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final DonationRepository donationRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getMyProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .accountId(account.getId())
                .username(account.getUsername())
                .status(account.getStatus());

        switch (account) {
            case Admin ignored -> builder.roleType("ADMIN");
            case Individual individual -> builder.roleType("INDIVIDUAL")
                    .email(individual.getEmail())
                    .phone(individual.getPhone())
                    .totalDonatedAmount(individual.getTotalDonatedAmount())
                    .fullName(individual.getFullName())
                    .address(individual.getAddress());
            case Organization org -> builder.roleType("ORGANIZATION")
                    .email(org.getEmail())
                    .phone(org.getPhone())
                    .totalDonatedAmount(org.getTotalDonatedAmount())
                    .orgName(org.getName())
                    .address(org.getAddress())
                    .websiteURL(org.getWebsiteURL())
                    .description(org.getDescription());
            default -> {
            }
        }

        return builder.build();
    }

    @Transactional
    public void updateMyProfile(String username, UpdateProfileRequest request) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        if (account instanceof Individual individual) {
            if (request.getPhone() != null) individual.setPhone(request.getPhone());
            if (request.getAddress() != null) individual.setAddress(request.getAddress());
            if (request.getFullName() != null) individual.setFullName(request.getFullName());
        }
        else if (account instanceof Organization org) {
            if (request.getPhone() != null) org.setPhone(request.getPhone());
            if (request.getAddress() != null) org.setAddress(request.getAddress());
            if (request.getName() != null) org.setName(request.getName());
            if (request.getWebsiteURL() != null) org.setWebsiteURL(request.getWebsiteURL());
            if (request.getDescription() != null) org.setDescription(request.getDescription());
            if (request.getEmail() != null) org.setEmail(request.getEmail());
            if (request.getPhone() != null) org.setPhone(request.getPhone());
        }

        accountRepository.save(account);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userDetailsByUsername", key = "#username")
    public void changePassword(String username, ChangePasswordRequest request) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác.");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu mới không khớp.");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    public Page<DonationHistoryResponse> getMyDonations(String username, int page, int size) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        if (account instanceof Admin) {
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Donation> donations = donationRepository.findByUser_IdOrderByDonationTimeDesc(account.getId(), pageRequest);

        return donations.map(donation -> DonationHistoryResponse.builder()
                .donationId(donation.getDonationId())
                .projectId(donation.getProject().getProjectId())
                .projectName(donation.getProject().getProjectName())
                .amount(donation.getAmount())
                .donationTime(donation.getDonationTime())
                .message(donation.getMessage())
                .status(donation.getStatus())
                .build()
        );
    }

    @Override
    public Page<TransactionHistoryResponse> getMyTransactions(String username, int page, int size) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        if (account instanceof Admin) {
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByDonation_User_IdOrderByCompletedAtDesc(account.getId(), pageRequest);

        return transactions.map(transaction -> {
            Donation donation = transaction.getDonation();
            return TransactionHistoryResponse.builder()
                    .transactionId(transaction.getTransactionId())
                    .donationId(donation.getDonationId())
                    .projectId(donation.getProject().getProjectId())
                    .projectName(donation.getProject().getProjectName())
                    .gatewayName(transaction.getGatewayName())
                    .gatewayTransactionNo(transaction.getGatewayTransactionNo())
                    .amount(transaction.getAmount())
                    .paymentStatus(transaction.getPaymentStatus())
                    .completedAt(transaction.getCompletedAt())
                    .donationTime(donation.getDonationTime())
                    .donationStatus(donation.getStatus())
                    .build();
        });
    }
}
