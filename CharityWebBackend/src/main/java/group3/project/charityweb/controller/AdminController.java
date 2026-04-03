package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.CategoryRequest;
import group3.project.charityweb.model.dto.request.UpdateStatusRequest;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 1. Thống kê tổng quan
    @GetMapping("/statistics")
    public ResponseEntity<SystemStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    // 2. Lấy danh sách Tổ chức chờ duyệt
    @GetMapping("/organizations/pending")
    public ResponseEntity<List<UserProfileResponse>> getPendingOrganizations() {
        return ResponseEntity.ok(adminService.getPendingOrganizations());
    }

    // 3. Duyệt/Từ chối Tổ chức
    @PutMapping("/organizations/{orgId}/verify")
    public ResponseEntity<?> verifyOrganization(@PathVariable String orgId, @RequestBody UpdateStatusRequest request) {
        adminService.updateOrganizationStatus(orgId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái tổ chức."));
    }

    // 4. Lấy danh sách Dự án chờ duyệt
    @GetMapping("/projects/pending")
    public ResponseEntity<List<ProjectResponse>> getPendingProjects() {
        return ResponseEntity.ok(adminService.getPendingProjects());
    }

    // 5. Duyệt/Từ chối Dự án
    @PutMapping("/projects/{projectId}/approve")
    public ResponseEntity<?> approveProject(@PathVariable String projectId, @RequestBody UpdateStatusRequest request) {
        adminService.updateProjectStatus(projectId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái dự án."));
    }

    // 6. Khóa/Mở khóa tài khoản người dùng
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String userId, @RequestBody UpdateStatusRequest request) {
        adminService.updateUserStatus(userId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái tài khoản."));
    }

    // 7. Thêm mới danh mục dự án
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        adminService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Đã tạo danh mục dự án mới."));
    }

    // 8. Chỉnh sửa danh mục dự án
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable String categoryId, @RequestBody CategoryRequest request) {
        adminService.updateCategory(categoryId, request);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật danh mục dự án."));
    }
}