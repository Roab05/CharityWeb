package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.UpdateActivityStatusRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.ProjectActivity;
import group3.project.charityweb.model.enums.ActivityStatus;
import group3.project.charityweb.repository.ActivityInteractionRepository;
import group3.project.charityweb.repository.ProjectActivityRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ProjectActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final ActivityInteractionRepository activityInteractionRepository;

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

    public Page<ActivityResponse> getProjectActivities(String projectId, int page, int size) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Dự án không tồn tại!");
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProjectActivity> activities = activityRepository.findByProject_ProjectId(projectId, pageRequest);

        return activities.map(this::mapToActivityResponse);
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

    @Override
    @Transactional
    public void deleteActivity(String username, String activityId) {
        ProjectActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng."));

        verifyActivityOwnership(username, activity);

        // Delete interactions first to avoid FK violations when removing activity.
        activityInteractionRepository.deleteByActivity_ActivityId(activityId);
        activityRepository.delete(activity);
    }

    private ActivityResponse mapToActivityResponse(ProjectActivity activity) {
        return ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .title(activity.getTitle())
                .content(activity.getContent())
                .imageURL(activity.getImageURL())
                .status(activity.getStatus())
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
