package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.DonationRequest;
import group3.project.charityweb.model.dto.response.DonationResponse;
import group3.project.charityweb.model.dto.response.PaymentUrlResponse;
import group3.project.charityweb.service.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    // POST /api/v1/projects/{projectId}/donations
    @PostMapping("/{projectId}/donations")
    public ResponseEntity<PaymentUrlResponse> createDonation(
            Principal principal,
            @PathVariable String projectId,
            @RequestBody DonationRequest request,
            HttpServletRequest httpRequest) { // Lấy httpRequest để truyền IP vào VNPAY
        PaymentUrlResponse response = donationService.initiateDonation(principal.getName(), projectId, request, httpRequest);
        return ResponseEntity.ok(response);
    }

    // GET /api/v1/projects/{projectId}/donations
    @GetMapping("/{projectId}/donations")
    public ResponseEntity<List<DonationResponse>> getProjectDonations(@PathVariable String projectId) {
        return ResponseEntity.ok(donationService.getProjectDonations(projectId));
    }
}