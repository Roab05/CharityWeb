package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.DuplicateResourceException;
import group3.project.charityweb.exception.InvalidDisbursementException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.dto.request.CategoryRequest;
import group3.project.charityweb.model.dto.request.UpdateStatusRequest;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.model.entity.*;
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
    public String createCategory(CategoryRequest request) {
        if (categoryRepository.existsById(request.getId())) {
            throw new DuplicateResourceException("Mã danh mục đã tồn tại!");
        }
        ProjectCategory category = new ProjectCategory();
        category.setId(request.getId()); // Hardcode ID dễ nhớ như EDU, HEALTH
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
        return disbursementRepository.findAllByStatus(0).stream()
                .map(this::mapToDisbursementResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateDisbursementStatus(String disbursementId, UpdateStatusRequest request) {
        // 1. Tìm bản ghi giải ngân
        Disbursement disbursement = disbursementRepository.findById(disbursementId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu giải ngân với ID: " + disbursementId));

        // 2. Nếu đã xử lý rồi (khác PENDING) thì không cho sửa nữa để tránh ghi đè dữ liệu
        if (disbursement.getStatus() != 0) {
            throw new InvalidDisbursementException("Yêu cầu giải ngân này đã được xử lý trước đó.");
        }

        // 3. Xử lý logic theo trạng thái mới
        if (request.getStatus() == 1) { // Duyệt giải ngân
            Project project = disbursement.getProject();

            // KIỂM TRA SỐ DƯ: Đảm bảo số tiền rút không lớn hơn số tiền dự án đang có
            if (project.getCurrentAmount().compareTo(disbursement.getAmount()) < 0) {
                throw new InvalidDisbursementException("Số dư dự án không đủ để thực hiện giải ngân này.");
            }

            // THỰC HIỆN TRỪ TIỀN: Cập nhật lại số tiền hiện tại của dự án
            project.setCurrentAmount(project.getCurrentAmount().subtract(disbursement.getAmount()));

            disbursement.setStatus(1); // Duyệt
            disbursement.setDisbursementTime(LocalDateTime.now()); // Ghi nhận thời gian tiền đi

            projectRepository.save(project);
        } else if (request.getStatus() == 2) { // Từ chối giải ngân
            disbursement.setStatus(2); // Thất bại/Từ chối
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