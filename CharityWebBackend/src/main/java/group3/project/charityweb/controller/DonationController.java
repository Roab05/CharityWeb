package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.DonationRequest;
import group3.project.charityweb.model.dto.response.DonationResponse;
import group3.project.charityweb.model.dto.response.PaymentUrlResponse;
import group3.project.charityweb.service.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/{projectId}/donations")
    public ResponseEntity<PaymentUrlResponse> createDonation(
            Principal principal,
            @PathVariable String projectId,
            @RequestBody DonationRequest request,
            HttpServletRequest httpRequest) {
        PaymentUrlResponse response = donationService.initiateDonation(principal.getName(), projectId, request, httpRequest);
        return ResponseEntity.ok(response);
    }

    // GET /api/v1/projects/{projectId}/donations
    @GetMapping("/{projectId}/donations")
    public ResponseEntity<Page<DonationResponse>> getProjectDonations(
            @PathVariable String projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(donationService.getProjectDonations(projectId, page, size));
    }
}