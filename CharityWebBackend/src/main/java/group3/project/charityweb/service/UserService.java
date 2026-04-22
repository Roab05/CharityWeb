package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.ChangePasswordRequest;
import group3.project.charityweb.model.dto.request.UpdateProfileRequest;
import group3.project.charityweb.model.dto.response.DonationHistoryResponse;
import group3.project.charityweb.model.dto.response.TransactionHistoryResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;

public interface UserService{
    UserProfileResponse getMyProfile(String username);
    void updateMyProfile(String username, UpdateProfileRequest request);
    Page<DonationHistoryResponse> getMyDonations(String username, int page, int size);
    Page<TransactionHistoryResponse> getMyTransactions(String username, int page, int size);
    void changePassword(String username, ChangePasswordRequest request);
}
