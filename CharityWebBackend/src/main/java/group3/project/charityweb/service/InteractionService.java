package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.InteractionRequest;
import group3.project.charityweb.model.dto.response.InteractionResponse;
import org.springframework.data.domain.Page;

public interface InteractionService {
    String createInteraction(String username, String activityId, InteractionRequest request);
    Page<InteractionResponse> getActivityInteractions(String activityId, int page, int size);
    void deleteInteraction(String username, String interactionId);
}