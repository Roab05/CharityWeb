package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.DisbursementRequest;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.service.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    // 1. Tạo hồ sơ giải ngân mới (POST /api/v1/projects/{projectId}/disbursements)
    @PostMapping("/projects/{projectId}/disbursements")
    public ResponseEntity<?> createDisbursement(
            Principal principal,
            @PathVariable String projectId,
            @RequestBody DisbursementRequest request) {

        disbursementService.createDisbursement(principal.getName(), projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tạo hồ sơ giải ngân thành công."));
    }

    // 2. Xem danh sách sao kê của dự án (GET /api/v1/projects/{projectId}/disbursements)
    @GetMapping("/projects/{projectId}/disbursements")
    public ResponseEntity<List<DisbursementResponse>> getProjectDisbursements(@PathVariable String projectId) {
        List<DisbursementResponse> responses = disbursementService.getProjectDisbursements(projectId);
        return ResponseEntity.ok(responses);
    }

    // 3. Xem chi tiết một khoản giải ngân (GET /api/v1/disbursements/{disbursementId})
    @GetMapping("/disbursements/{disbursementId}")
    public ResponseEntity<DisbursementResponse> getDisbursementDetails(@PathVariable String disbursementId) {
        DisbursementResponse response = disbursementService.getDisbursementById(disbursementId);
        return ResponseEntity.ok(response);
    }
}