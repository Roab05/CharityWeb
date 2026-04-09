package group3.project.charityweb.service.impl;

import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.service.ProjectService;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.CreateProjectRequest;
import group3.project.charityweb.model.entity.Organization;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.ProjectActivity;
import group3.project.charityweb.model.entity.ProjectCategory;
import group3.project.charityweb.repository.OrganizationRepository;
import group3.project.charityweb.repository.ProjectActivityRepository;
import group3.project.charityweb.repository.ProjectCategoryRepository;
import group3.project.charityweb.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectCategoryRepository categoryRepository;
    private final ProjectActivityRepository activityRepository;

    // 1. Tạo dự án mới (Tổ chức)
    @Transactional
    public String createProject(String username, CreateProjectRequest request) {
        Organization org = organizationRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Tài khoản không phải Tổ chức!"));

        Project project = new Project();
        project.setProjectName(request.getProjectName());
        project.setCreatedAt(LocalDateTime.now());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setDescription(request.getDescription());
        project.setTargetAmount(request.getTargetAmount());
        project.setCurrentAmount(BigDecimal.ZERO);
        project.setStatus(2); // 2: Pending (Chờ Admin duyệt)
        project.setBackgroundImageURL(request.getBackgroundImageURL());
        project.setBankAccountNo(request.getBankAccountNo());

        // Ánh xạ Danh mục
        List<ProjectCategory> categories = categoryRepository.findAllById(request.getCategoryIds());
        project.setCategories(categories);

        // Thiết lập chủ sở hữu (ManyToMany như sơ đồ DB)
        project.setOrganizations(List.of(org));

        projectRepository.save(project);
        return project.getProjectId();
    }

    // 2. Lấy danh sách dự án (Public - Có phân trang và lọc)
    public Page<ProjectResponse> getAllProjects(Integer status, String categoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Project> projectPage;

        if (categoryId != null && !categoryId.isEmpty()) {
            projectPage = projectRepository.findByStatusAndCategories_Id(status != null ? status : 1, categoryId, pageRequest);
        } else {
            projectPage = projectRepository.findByStatus(status != null ? status : 1, pageRequest);
        }

        // Convert Page<Project> thành Page<ProjectResponse>
        return projectPage.map(this::mapToProjectResponse);
    }

    // 3. Xem chi tiết dự án
    public ProjectResponse getProjectResponseById(String projectId) {
        Project project = getProjectEntityById(projectId);
        return mapToProjectResponse(project);
    }

    // 4. Đăng cập nhật tiến độ
    @Transactional
    public String createActivity(String username, String projectId, ActivityRequest request) {
        Project project = getProjectEntityById(projectId); // Dùng hàm private gọi entity

        boolean isOwner = project.getOrganizations().stream()
                .anyMatch(org -> org.getUsername().equals(username));

        if (!isOwner) {
            throw new UnauthorizedAccessException("Bạn không có quyền đăng tải hoạt động cho dự án này!");
        }

        ProjectActivity activity = new ProjectActivity();
        activity.setTitle(request.getTitle());
        activity.setContent(request.getContent());
        activity.setImageURL(request.getImageURL());
        activity.setProject(project);

        activityRepository.save(activity);
        return activity.getActivityId();
    }

    // 5. Lấy dòng thời gian hoạt động của dự án
    public List<ActivityResponse> getProjectActivities(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Dự án không tồn tại!");
        }

        List<ProjectActivity> activities = activityRepository.findByProject_ProjectId(projectId);

        return activities.stream()
                .map(this::mapToActivityResponse)
                .collect(Collectors.toList());
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .createdAt(project.getCreatedAt())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .description(project.getDescription())
                .targetAmount(project.getTargetAmount())
                .currentAmount(project.getCurrentAmount())
                .status(project.getStatus())
                .backgroundImageURL(project.getBackgroundImageURL())
                .bankAccountNo(project.getBankAccountNo())
                .categories(project.getCategories().stream()
                        .map(ProjectCategory::getCategoryName)
                        .collect(Collectors.toList()))
                .organizationNames(project.getOrganizations().stream()
                        .map(Organization::getName)
                        .collect(Collectors.toList()))
                .build();
    }

    private ActivityResponse mapToActivityResponse(ProjectActivity activity) {
        return ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .title(activity.getTitle())
                .content(activity.getContent())
                .imageURL(activity.getImageURL())
                .build();
    }

    // Hàm private dùng nội bộ để truy vấn Entity phục vụ logic update/delete
    private Project getProjectEntityById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));
    }
}
