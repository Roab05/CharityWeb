package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.AddProjectOrganizationRequest;
import group3.project.charityweb.model.dto.request.CreateProjectRequest;
import group3.project.charityweb.model.dto.response.OrganizationSelectorResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import group3.project.charityweb.model.enums.ProjectStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectService {
    String createProject(String username, CreateProjectRequest request);
    Page<ProjectResponse> getAllProjects(ProjectStatus status, String categoryId, int page, int size);
    ProjectResponse getProjectResponseById(String projectId);
    Page<ProjectResponse> getMyManagedProjects(String username, ProjectStatus status, int page, int size);
    List<OrganizationSelectorResponse> getOrganizationsForSelector(String name);
    void addOrganizationToProject(String username, String projectId, AddProjectOrganizationRequest request);
}
