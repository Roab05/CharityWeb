package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.*;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/statistics")
    public ResponseEntity<SystemStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    @GetMapping("/organizations/pending")
    public ResponseEntity<Page<UserProfileResponse>> getPendingOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getPendingOrganizations(page, size));
    }

    @PutMapping("/organizations/{orgId}/verify")
    public ResponseEntity<?> verifyOrganization(@PathVariable String orgId, @RequestBody UpdateAccountStatusRequest request) {
        adminService.updateOrganizationStatus(orgId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái tổ chức."));
    }

    @GetMapping("/projects/pending")
    public ResponseEntity<Page<ProjectResponse>> getPendingProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getPendingProjects(page, size));
    }

    @PutMapping("/projects/{projectId}/approve")
    public ResponseEntity<?> approveProject(@PathVariable String projectId, @RequestBody UpdateProjectStatusRequest request) {
        adminService.updateProjectStatus(projectId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái dự án."));
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String userId, @RequestBody UpdateAccountStatusRequest request) {
        adminService.updateUserStatus(userId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái tài khoản."));
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        String categoryId = adminService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Đã tạo danh mục dự án mới.",
                        "id", categoryId
                ));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable String categoryId, @RequestBody CategoryRequest request) {
        adminService.updateCategory(categoryId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật danh mục dự án."));
    }

    @GetMapping("/disbursements/pending")
    public ResponseEntity<Page<DisbursementResponse>> getPendingDisbursements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getPendingDisbursements(page, size));
    }

    @PutMapping("/disbursements/{disbursementId}/status")
    public ResponseEntity<?> updateDisbursementStatus(
            @PathVariable String disbursementId,
            @RequestBody UpdateDisbursementStatusRequest request) {

        adminService.updateDisbursementStatus(disbursementId, request);

        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái yêu cầu giải ngân."));
    }
}