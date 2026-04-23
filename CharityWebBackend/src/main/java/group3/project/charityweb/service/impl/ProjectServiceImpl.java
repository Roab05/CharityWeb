package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.DuplicateResourceException;
import group3.project.charityweb.model.dto.request.AddProjectOrganizationRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.dto.response.OrganizationSelectorResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.enums.AccountStatus;
import group3.project.charityweb.model.enums.ProjectStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectCategoryRepository categoryRepository;

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
        project.setStatus(ProjectStatus.PENDING);
        project.setBackgroundImageURL(request.getBackgroundImageURL());
        project.setBankAccountNo(request.getBankAccountNo());

        List<ProjectCategory> categories = categoryRepository.findAllById(request.getCategoryIds());
        project.setCategories(categories);

        project.setOrganizations(List.of(org));

        projectRepository.save(project);
        return project.getProjectId();
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(ProjectStatus status, String categoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Project> projectPage;

        if (categoryId != null && !categoryId.isEmpty()) {
            projectPage = projectRepository.findByStatusAndCategories_Id(status != null ? status : ProjectStatus.ACTIVE, categoryId, pageRequest);
        } else {
            projectPage = projectRepository.findByStatus(status != null ? status : ProjectStatus.ACTIVE, pageRequest);
        }

        return projectPage.map(this::mapToProjectResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getMyManagedProjects(String username, ProjectStatus status, int page, int size) {
        organizationRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Tài khoản không phải Tổ chức!"));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Project> projectPage = status == null
                ? projectRepository.findByOrganizations_UsernameOrderByCreatedAtDesc(username, pageRequest)
                : projectRepository.findByOrganizations_UsernameAndStatusOrderByCreatedAtDesc(username, status, pageRequest);

        return projectPage.map(this::mapToProjectResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationSelectorResponse> getOrganizationsForSelector(String name) {
        List<Organization> organizations = (name == null || name.isBlank())
                ? organizationRepository.findByStatusOrderByCreatedAtDesc(AccountStatus.ACTIVE)
                : organizationRepository.findByStatusAndNameContainingIgnoreCaseOrderByCreatedAtDesc(AccountStatus.ACTIVE, name.trim());

        return organizations.stream()
                .map(org -> OrganizationSelectorResponse.builder()
                        .organizationId(org.getId())
                        .name(org.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addOrganizationToProject(String username, String projectId, AddProjectOrganizationRequest request) {
        if (request == null || request.getOrganizationId() == null || request.getOrganizationId().isBlank()) {
            throw new ResourceNotFoundException("Thiếu organizationId để thêm vào dự án.");
        }

        Organization actor = organizationRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Tài khoản không phải Tổ chức!"));
        Project project = getProjectEntityById(projectId);

        boolean isManager = project.getOrganizations().stream()
                .anyMatch(org -> org.getId().equals(actor.getId()));
        if (!isManager) {
            throw new UnauthorizedAccessException("Bạn không có quyền thêm tổ chức vào dự án này!");
        }

        Organization target = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tổ chức cần thêm!"));

        if (target.getStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Chỉ có thể thêm tổ chức đang hoạt động vào dự án.");
        }

        List<Organization> organizations = new ArrayList<>(project.getOrganizations() == null ? List.of() : project.getOrganizations());
        boolean alreadyManaged = organizations.stream().anyMatch(org -> org.getId().equals(target.getId()));
        if (alreadyManaged) {
            throw new DuplicateResourceException("Tổ chức đã nằm trong danh sách quản lý dự án.");
        }

        organizations.add(target);
        project.setOrganizations(organizations);
        projectRepository.save(project);
    }

    public ProjectResponse getProjectResponseById(String projectId) {
        Project project = getProjectEntityById(projectId);
        return mapToProjectResponse(project);
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

    private Project getProjectEntityById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));
    }
}
