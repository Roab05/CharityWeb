package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.InteractionRequest;
import group3.project.charityweb.model.dto.response.InteractionResponse;

import java.util.List;

public interface InteractionService {
    String createInteraction(String username, String activityId, InteractionRequest request);
    List<InteractionResponse> getActivityInteractions(String activityId);
    void deleteInteraction(String username, String interactionId);
}