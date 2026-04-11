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

    @PostMapping("/projects/{projectId}/disbursements")
    public ResponseEntity<?> createDisbursement(
            Principal principal,
            @PathVariable String projectId,
            @RequestBody DisbursementRequest request) {

        String disbursementId = disbursementService.createDisbursement(principal.getName(), projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Tạo hồ sơ giải ngân thành công.",
                        "id", disbursementId
                ));
    }

    @GetMapping("/projects/{projectId}/disbursements")
    public ResponseEntity<List<DisbursementResponse>> getProjectDisbursements(@PathVariable String projectId) {
        List<DisbursementResponse> responses = disbursementService.getProjectDisbursements(projectId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/disbursements/{disbursementId}")
    public ResponseEntity<DisbursementResponse> getDisbursementDetails(@PathVariable String disbursementId) {
        DisbursementResponse response = disbursementService.getDisbursementById(disbursementId);
        return ResponseEntity.ok(response);
    }
}