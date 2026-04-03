package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.DonationRequest;
import group3.project.charityweb.model.dto.response.DonationResponse;
import group3.project.charityweb.model.dto.response.PaymentUrlResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DonationService {
    PaymentUrlResponse initiateDonation(String username, String projectId, DonationRequest request, HttpServletRequest httpRequest);
    List<DonationResponse> getProjectDonations(String projectId);
}