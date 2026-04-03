package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.UpdateProfileRequest;
import group3.project.charityweb.model.dto.response.DonationHistoryResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
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
    public ResponseEntity<List<DonationHistoryResponse>> getMyDonations(Principal principal) {
        List<DonationHistoryResponse> responses = userService.getMyDonations(principal.getName());
        return ResponseEntity.ok(responses);
    }
}