package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.UpdateActivityStatusRequest;
import group3.project.charityweb.model.dto.response.ActivityResponse;

import java.util.List;

public interface ActivityService {
    String createActivity(String username, String projectId, ActivityRequest request);
    List<ActivityResponse> getProjectActivities(String projectId);
    void updateActivityStatus(String username, String activityId, UpdateActivityStatusRequest request);
    void updateActivity(String username, String activityId, ActivityRequest request);
}
