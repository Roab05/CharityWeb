package group3.project.charityweb.service.impl;

import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.dto.response.ProjectCategoryResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.enums.ProjectStatus;
import group3.project.charityweb.service.ProjectService;
import group3.project.charityweb.exception.InvalidDisbursementException;
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
import java.util.Collections;
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

        if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
            throw new InvalidDisbursementException("Vui lòng chọn ít nhất 1 danh mục cho dự án.");
        }

        List<ProjectCategory> categories = categoryRepository.findAllById(request.getCategoryIds());
        if (categories.size() != request.getCategoryIds().size()) {
            throw new InvalidDisbursementException("Một hoặc nhiều danh mục không tồn tại.");
        }
        project.setCategories(categories);

        project.setOrganizations(List.of(org));

        projectRepository.save(project);
        return project.getProjectId();
    }

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

    public ProjectResponse getProjectResponseById(String projectId) {
        Project project = getProjectEntityById(projectId);
        return mapToProjectResponse(project);
    }

    public List<ProjectCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> ProjectCategoryResponse.builder()
                        .id(category.getId())
                        .categoryName(category.getCategoryName())
                        .description(category.getDescription())
                        .build())
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

    private Project getProjectEntityById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));
    }
}
