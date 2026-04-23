package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.AddProjectOrganizationRequest;
import group3.project.charityweb.model.dto.request.CreateProjectRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.dto.response.OrganizationSelectorResponse;
import group3.project.charityweb.model.dto.response.ProjectCategoryResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.enums.ProjectStatus;
import group3.project.charityweb.service.ActivityService;
import group3.project.charityweb.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<?> createProject(Principal principal, @RequestBody CreateProjectRequest request) {
        String projectId = projectService.createProject(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Tạo dự án thành công, vui lòng chờ Admin phê duyệt.",
                        "id", projectId
                ));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @RequestParam(required = false, defaultValue = "ACTIVE") ProjectStatus status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProjectResponse> projects = projectService.getAllProjects(status, categoryId, page, size);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/me/managed")
    public ResponseEntity<Page<ProjectResponse>> getMyManagedProjects(
            Principal principal,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProjectResponse> projects = projectService.getMyManagedProjects(principal.getName(), status, page, size);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectDetails(@PathVariable String projectId) {
        ProjectResponse project = projectService.getProjectResponseById(projectId);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/organizations")
    public ResponseEntity<List<OrganizationSelectorResponse>> getOrganizationsForSelector(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(projectService.getOrganizationsForSelector(name));
    }

    @PostMapping("/{projectId}/organizations")
    public ResponseEntity<?> addOrganizationToProject(
            Principal principal,
            @PathVariable String projectId,
            @RequestBody AddProjectOrganizationRequest request) {
        projectService.addOrganizationToProject(principal.getName(), projectId, request);
        return ResponseEntity.ok(Map.of("message", "Đã thêm tổ chức vào danh sách quản lý dự án."));
    }

    @PostMapping("/{projectId}/activities")
    public ResponseEntity<?> createActivity(Principal principal,
                                            @PathVariable String projectId,
                                            @RequestBody ActivityRequest request) {
        String activityId = activityService.createActivity(principal.getName(), projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Đã cập nhật tiến độ dự án.",
                        "id", activityId
                ));
    }

    @GetMapping("/{projectId}/activities")
    public ResponseEntity<Page<ActivityResponse>> getActivities(
            @PathVariable String projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityResponse> activities = activityService.getProjectActivities(projectId, page, size);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/categories")
    public ResponseEntity<Page<ProjectCategoryResponse>> getCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProjectCategoryResponse> categories = projectService.searchCategories(keyword, page, size);
        return ResponseEntity.ok(categories);
    }
}