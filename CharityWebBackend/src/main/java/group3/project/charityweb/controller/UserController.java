package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.ChangePasswordRequest;
import group3.project.charityweb.model.dto.request.UpdateProfileRequest;
import group3.project.charityweb.model.dto.response.DonationHistoryResponse;
import group3.project.charityweb.model.dto.response.TransactionHistoryResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Principal principal) {
        UserProfileResponse response = userService.getMyProfile(principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(Principal principal, @RequestBody UpdateProfileRequest request) {
        userService.updateMyProfile(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Cập nhật hồ sơ thành công"));
    }

    @GetMapping("/me/donations")
    public ResponseEntity<Page<DonationHistoryResponse>> getMyDonations(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DonationHistoryResponse> responses = userService.getMyDonations(principal.getName(), page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me/transactions")
    public ResponseEntity<Page<TransactionHistoryResponse>> getMyTransactions(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionHistoryResponse> responses = userService.getMyTransactions(principal.getName(), page, size);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
    }
}