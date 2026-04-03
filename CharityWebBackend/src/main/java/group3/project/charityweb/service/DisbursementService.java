package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.DisbursementRequest;
import group3.project.charityweb.model.dto.response.DisbursementResponse;

import java.util.List;

public interface DisbursementService {
    void createDisbursement(String username, String projectId, DisbursementRequest request);
    List<DisbursementResponse> getProjectDisbursements(String projectId);
    DisbursementResponse getDisbursementById(String disbursementId);
}