package group3.project.charityweb.service.impl;

import group3.project.charityweb.service.UserService;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.UpdateProfileRequest;
import group3.project.charityweb.model.dto.response.DonationHistoryResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final DonationRepository donationRepository;

    // 1. Lấy thông tin User hiện tại
    public UserProfileResponse getMyProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .accountId(account.getId())
                .username(account.getUsername())
                .status(account.getStatus());

        if (account instanceof Admin) {
            builder.roleType("ADMIN");
        } else if (account instanceof Individual individual) {
            builder.roleType("INDIVIDUAL")
                    .email(individual.getEmail())
                    .phone(individual.getPhone())
                    .totalDonatedAmount(individual.getTotalDonatedAmount())
                    .fullName(individual.getFullName())
                    .address(individual.getAddress());
        } else if (account instanceof Organization org) {
            builder.roleType("ORGANIZATION")
                    .email(org.getEmail())
                    .phone(org.getPhone())
                    .totalDonatedAmount(org.getTotalDonatedAmount())
                    .orgName(org.getName())
                    .address(org.getAddress())
                    .websiteURL(org.getWebsiteURL())
                    .description(org.getDescription());
        }

        return builder.build();
    }

    // 2. Cập nhật thông tin User hiện tại
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
            if (request.getContactEmail() != null) org.setContactEmail(request.getContactEmail());
            if (request.getContactPhone() != null) org.setContactPhone(request.getContactPhone());
        }

        accountRepository.save(account);
    }

    // 3. Lấy lịch sử quyên góp
    public List<DonationHistoryResponse> getMyDonations(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        // Admin không có quyên góp
        if (account instanceof Admin) {
            return List.of();
        }

        List<Donation> donations = donationRepository.findByUser_IdOrderByDonationTimeDesc(account.getId());

        return donations.stream().map(donation -> DonationHistoryResponse.builder()
                .donationId(donation.getDonationId())
                .projectId(donation.getProject().getProjectId())
                .projectName(donation.getProject().getDescription()) // Giả sử dùng description làm tên ngắn, bạn có thể thiết kế thêm trường projectName trong Project nếu cần
                .amount(donation.getAmount())
                .donationTime(donation.getDonationTime())
                .message(donation.getMessage())
                .status(donation.getStatus())
                .build()
        ).collect(Collectors.toList());
    }
}
