package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.*;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;

import java.util.List;

public interface AdminService {
    SystemStatisticsResponse getStatistics();

    List<UserProfileResponse> getPendingOrganizations();
    void updateOrganizationStatus(String orgId, UpdateAccountStatusRequest request);

    List<ProjectResponse> getPendingProjects();
    void updateProjectStatus(String projectId, UpdateProjectStatusRequest request);

    void updateUserStatus(String userId, UpdateAccountStatusRequest request);

    String createCategory(CategoryRequest request);
    void updateCategory(String categoryId, CategoryRequest request);

    List<DisbursementResponse> getPendingDisbursements();
    void updateDisbursementStatus(String disbursementId, UpdateDisbursementStatusRequest request);
}