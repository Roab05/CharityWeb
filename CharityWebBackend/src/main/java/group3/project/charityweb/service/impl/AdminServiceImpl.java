package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.DuplicateResourceException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.CategoryRequest;
import group3.project.charityweb.model.dto.request.UpdateStatusRequest;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.model.entity.Account;
import group3.project.charityweb.model.entity.Organization;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.ProjectCategory;
import group3.project.charityweb.repository.*;
import group3.project.charityweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountRepository accountRepository;
    private final ProjectCategoryRepository categoryRepository;

    @Override
    public SystemStatisticsResponse getStatistics() {
        return SystemStatisticsResponse.builder()
                .totalDonatedAmount(donationRepository.sumAllSuccessfulDonations())
                .activeProjectsCount(projectRepository.countByStatus(1))
                .pendingOrganizationsCount(organizationRepository.countByStatus(2))
                .pendingProjectsCount(projectRepository.countByStatus(2))
                .build();
    }

    @Override
    public List<UserProfileResponse> getPendingOrganizations() {
        List<Organization> pendingOrgs = organizationRepository.findByStatusOrderByCreatedAtDesc(2);
        return pendingOrgs.stream().map(org -> UserProfileResponse.builder()
                .accountId(org.getId())
                .username(org.getUsername())
                .email(org.getEmail())
                .phone(org.getPhone())
                .orgName(org.getName())
                .websiteURL(org.getWebsiteURL())
                .description(org.getDescription())
                .status(org.getStatus())
                .roleType("ORGANIZATION")
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrganizationStatus(String orgId, UpdateStatusRequest request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tổ chức!"));
        org.setStatus(request.getStatus());
        organizationRepository.save(org);
    }

    @Override
    public List<ProjectResponse> getPendingProjects() {
        List<Project> pendingProjects = projectRepository.findByStatusOrderByCreatedAtDesc(2);
        return pendingProjects.stream().map(project -> ProjectResponse.builder()
                .projectId(project.getProjectId())
                .createdAt(project.getCreatedAt())
                .description(project.getDescription())
                .targetAmount(project.getTargetAmount())
                .status(project.getStatus())
                .organizationNames(project.getOrganizations().stream().map(Organization::getName).collect(Collectors.toList()))
                // Bổ sung các field cần thiết khác nếu muốn
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProjectStatus(String projectId, UpdateStatusRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Dự án!"));
        project.setStatus(request.getStatus());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void updateUserStatus(String userId, UpdateStatusRequest request) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng!"));
        account.setStatus(request.getStatus()); // 0 = Khóa, 1 = Mở khóa
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void createCategory(CategoryRequest request) {
        if (categoryRepository.existsById(request.getId())) {
            throw new DuplicateResourceException("Mã danh mục đã tồn tại!");
        }
        ProjectCategory category = new ProjectCategory();
        category.setId(request.getId()); // Hardcode ID dễ nhớ như EDU, HEALTH
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(String categoryId, CategoryRequest request) {
        ProjectCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục!"));
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
    }
}