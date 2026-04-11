package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.DuplicateResourceException;
import group3.project.charityweb.exception.InvalidDisbursementException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.*;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.model.enums.AccountStatus;
import group3.project.charityweb.model.enums.DisbursementStatus;
import group3.project.charityweb.model.enums.DonationStatus;
import group3.project.charityweb.model.enums.ProjectStatus;
import group3.project.charityweb.repository.*;
import group3.project.charityweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final DisbursementRepository disbursementRepository;

    @Override
    public SystemStatisticsResponse getStatistics() {
        return SystemStatisticsResponse.builder()
                .totalDonatedAmount(donationRepository.sumAllSuccessfulDonations(DonationStatus.SUCCESS))
                .activeProjectsCount(projectRepository.countByStatus(ProjectStatus.ACTIVE))
                .pendingOrganizationsCount(organizationRepository.countByStatus(AccountStatus.PENDING))
                .pendingProjectsCount(projectRepository.countByStatus(ProjectStatus.PENDING))
                .build();
    }

    @Override
    public List<UserProfileResponse> getPendingOrganizations() {
        List<Organization> pendingOrgs = organizationRepository.findByStatusOrderByCreatedAtDesc(AccountStatus.PENDING);
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
    public void updateOrganizationStatus(String orgId, UpdateAccountStatusRequest request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tổ chức!"));
        org.setStatus(request.getStatus());
        organizationRepository.save(org);
    }

    @Override
    public List<ProjectResponse> getPendingProjects() {
        List<Project> pendingProjects = projectRepository.findByStatusOrderByCreatedAtDesc(ProjectStatus.PENDING);
        return pendingProjects.stream().map(project -> ProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdAt(project.getCreatedAt())
                .description(project.getDescription())
                .targetAmount(project.getTargetAmount())
                .status(project.getStatus())
                .organizationNames(project.getOrganizations().stream().map(Organization::getName).collect(Collectors.toList()))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProjectStatus(String projectId, UpdateProjectStatusRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Dự án!"));
        project.setStatus(request.getStatus());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void updateUserStatus(String userId, UpdateAccountStatusRequest request) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng!"));
        account.setStatus(request.getStatus());
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public String createCategory(CategoryRequest request) {
        if (categoryRepository.existsById(request.getId())) {
            throw new DuplicateResourceException("Mã danh mục đã tồn tại!");
        }
        ProjectCategory category = new ProjectCategory();
        category.setId(request.getId());
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
        return category.getId();
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

    @Override
    @Transactional(readOnly = true)
    public List<DisbursementResponse> getPendingDisbursements() {
        return disbursementRepository.findAllByStatus(DisbursementStatus.PENDING).stream()
                .map(this::mapToDisbursementResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateDisbursementStatus(String disbursementId, UpdateDisbursementStatusRequest request) {
        Disbursement disbursement = disbursementRepository.findById(disbursementId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu giải ngân với ID: " + disbursementId));

        if (disbursement.getStatus() != DisbursementStatus.PENDING) {
            throw new InvalidDisbursementException("Yêu cầu giải ngân này đã được xử lý trước đó.");
        }

        // 3. Xử lý logic theo trạng thái mới
        if (request.getStatus() == DisbursementStatus.PENDING) {
            Project project = disbursement.getProject();

            if (project.getCurrentAmount().compareTo(disbursement.getAmount()) < 0) {
                throw new InvalidDisbursementException("Số dư dự án không đủ để thực hiện giải ngân này.");
            }

            project.setCurrentAmount(project.getCurrentAmount().subtract(disbursement.getAmount()));

            disbursement.setStatus(DisbursementStatus.APPROVED); // Duyệt
            disbursement.setDisbursementTime(LocalDateTime.now()); // Ghi nhận thời gian tiền đi

            projectRepository.save(project);
        } else if (request.getStatus() == DisbursementStatus.REJECTED) { // Từ chối giải ngân
            disbursement.setStatus(DisbursementStatus.REJECTED); // Thất bại/Từ chối
        } else {
            throw new InvalidDisbursementException("Trạng thái cập nhật không hợp lệ.");
        }

        disbursementRepository.save(disbursement);
    }

    // Hàm helper để convert Entity sang DTO
    private DisbursementResponse mapToDisbursementResponse(Disbursement entity) {
        return DisbursementResponse.builder()
                .disbursementId(entity.getDisbursementId()) //
                .projectId(entity.getProject().getProjectId()) //
                .amount(entity.getAmount()) //
                .disbursementTime(entity.getDisbursementTime()) //
                .reason(entity.getReason()) //
                .evidenceURL(entity.getEvidenceURL()) //
                .recipientInfo(entity.getRecipientInfo()) //
                .status(entity.getStatus()) //
                .build();
    }
}