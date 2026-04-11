package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.UpdateActivityStatusRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.ProjectActivity;
import group3.project.charityweb.model.enums.ActivityStatus;
import group3.project.charityweb.repository.ProjectActivityRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ProjectActivityRepository activityRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public String createActivity(String username, String projectId, ActivityRequest request) {
        Project project = getProjectEntityById(projectId);

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
        activity.setStatus(ActivityStatus.VISIBLE);

        activityRepository.save(activity);
        return activity.getActivityId();
    }

    public List<ActivityResponse> getProjectActivities(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Dự án không tồn tại!");
        }

        List<ProjectActivity> activities = activityRepository.findByProject_ProjectId(projectId);

        return activities.stream()
                .map(this::mapToActivityResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateActivityStatus(String username, String activityId, UpdateActivityStatusRequest request) {
        ProjectActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng."));

        verifyActivityOwnership(username, activity);

        activity.setStatus(request.getStatus());
        activityRepository.save(activity);
    }

    @Override
    @Transactional
    public void updateActivity(String username, String activityId, ActivityRequest request) {
        ProjectActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng."));

        verifyActivityOwnership(username, activity);

        if (request.getTitle() != null) {
            activity.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            activity.setContent(request.getContent());
        }
        if (request.getImageURL() != null) {
            activity.setImageURL(request.getImageURL());
        }

        activityRepository.save(activity);
    }

    private ActivityResponse mapToActivityResponse(ProjectActivity activity) {
        return ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .title(activity.getTitle())
                .content(activity.getContent())
                .imageURL(activity.getImageURL())
                .build();
    }

    private Project getProjectEntityById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));
    }

    private void verifyActivityOwnership(String username, ProjectActivity activity) {
        Project project = activity.getProject();
        boolean isCoOrganizer = project.getOrganizations().stream()
                .anyMatch(org -> org.getUsername().equals(username));

        if (!isCoOrganizer) {
            throw new RuntimeException("Truy cập bị từ chối: Bạn không phải là đơn vị đồng tổ chức của dự án này.");
        }
    }
}
