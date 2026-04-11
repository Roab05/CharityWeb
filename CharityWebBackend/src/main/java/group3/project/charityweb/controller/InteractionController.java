package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.InteractionRequest;
import group3.project.charityweb.model.dto.response.InteractionResponse;
import group3.project.charityweb.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping("/activities/{activityId}/interactions")
    public ResponseEntity<?> createInteraction(
            Principal principal,
            @PathVariable String activityId,
            @RequestBody InteractionRequest request) {

        String interactionId = interactionService.createInteraction(principal.getName(), activityId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Đã gửi tương tác thành công.",
                        "id", interactionId
                ));
    }

    @GetMapping("/activities/{activityId}/interactions")
    public ResponseEntity<List<InteractionResponse>> getActivityInteractions(@PathVariable String activityId) {
        List<InteractionResponse> responses = interactionService.getActivityInteractions(activityId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/interactions/{interactionId}")
    public ResponseEntity<?> deleteInteraction(Principal principal, @PathVariable String interactionId) {

        interactionService.deleteInteraction(principal.getName(), interactionId);

        return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận thành công."));
    }
}