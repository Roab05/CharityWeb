package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.UpdateActivityStatusRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;
import org.springframework.data.domain.Page;

public interface ActivityService {
    String createActivity(String username, String projectId, ActivityRequest request);
    Page<ActivityResponse> getProjectActivities(String projectId, int page, int size);
    void updateActivityStatus(String username, String activityId, UpdateActivityStatusRequest request);
    void updateActivity(String username, String activityId, ActivityRequest request);
    void deleteActivity(String username, String activityId);
}
