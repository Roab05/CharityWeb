package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.DonationRequest;
import group3.project.charityweb.model.dto.response.DonationResponse;
import group3.project.charityweb.model.dto.response.PaymentUrlResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface DonationService {
    PaymentUrlResponse initiateDonation(String username, String projectId, DonationRequest request, HttpServletRequest httpRequest);
    Page<DonationResponse> getProjectDonations(String projectId, int page, int size);
}