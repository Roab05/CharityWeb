package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.*;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;

public interface AdminService {
    SystemStatisticsResponse getStatistics();

    Page<UserProfileResponse> getPendingOrganizations(int page, int size);
    void updateOrganizationStatus(String orgId, UpdateAccountStatusRequest request);

    Page<ProjectResponse> getPendingProjects(int page, int size);
    void updateProjectStatus(String projectId, UpdateProjectStatusRequest request);

    void updateUserStatus(String userId, UpdateAccountStatusRequest request);

    String createCategory(CategoryRequest request);
    void updateCategory(String categoryId, CategoryRequest request);

    Page<DisbursementResponse> getPendingDisbursements(int page, int size);
    void updateDisbursementStatus(String disbursementId, UpdateDisbursementStatusRequest request);
}