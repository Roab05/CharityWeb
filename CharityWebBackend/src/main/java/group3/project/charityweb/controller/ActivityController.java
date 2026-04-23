package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.ActivityRequest;
import group3.project.charityweb.model.dto.request.UpdateActivityStatusRequest;
import group3.project.charityweb.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PutMapping("/{activityId}/status")
    public ResponseEntity<?> updateActivityStatus(
            Principal principal,
            @PathVariable String activityId,
            @RequestBody UpdateActivityStatusRequest request) {

        activityService.updateActivityStatus(principal.getName(), activityId, request);

        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái bài đăng."));
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<?> updateActivityContent(
            Principal principal,
            @PathVariable String activityId,
            @RequestBody ActivityRequest request) {

        activityService.updateActivity(principal.getName(), activityId, request);

        return ResponseEntity.ok(Map.of("message", "Cập nhật bài đăng thành công."));
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<?> deleteActivity(
            Principal principal,
            @PathVariable String activityId) {

        activityService.deleteActivity(principal.getName(), activityId);

        return ResponseEntity.ok(Map.of("message", "Đã xóa bài đăng thành công."));
    }
}