package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.CreateProjectRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectService {
    String createProject(String username, CreateProjectRequest request);
    Page<ProjectResponse> getAllProjects(Integer status, String categoryId, int page, int size);
    ProjectResponse getProjectResponseById(String projectId);
    String createActivity(String username, String projectId, ActivityRequest request);
    List<ActivityResponse> getProjectActivities(String projectId);
}
