package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.CategoryRequest;
import group3.project.charityweb.model.dto.request.UpdateStatusRequest;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;

import java.util.List;

public interface AdminService {
    SystemStatisticsResponse getStatistics();

    List<UserProfileResponse> getPendingOrganizations();
    void updateOrganizationStatus(String orgId, UpdateStatusRequest request);

    List<ProjectResponse> getPendingProjects();
    void updateProjectStatus(String projectId, UpdateStatusRequest request);

    void updateUserStatus(String userId, UpdateStatusRequest request);

    void createCategory(CategoryRequest request);
    void updateCategory(String categoryId, CategoryRequest request);
}