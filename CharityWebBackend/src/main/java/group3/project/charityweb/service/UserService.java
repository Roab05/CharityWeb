package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.ChangePasswordRequest;
import group3.project.charityweb.model.dto.request.UpdateProfileRequest;
import group3.project.charityweb.model.dto.response.DonationHistoryResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService{
    UserProfileResponse getMyProfile(String username);
    void updateMyProfile(String username, UpdateProfileRequest request);
    List<DonationHistoryResponse> getMyDonations(String username);
    void changePassword(String username, ChangePasswordRequest request);
}
