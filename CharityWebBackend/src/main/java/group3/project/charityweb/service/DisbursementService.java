package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.DisbursementRequest;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import org.springframework.data.domain.Page;

public interface DisbursementService {
    String createDisbursement(String username, String projectId, DisbursementRequest request);
    Page<DisbursementResponse> getProjectDisbursements(String projectId, int page, int size);
    DisbursementResponse getDisbursementById(String disbursementId);
}