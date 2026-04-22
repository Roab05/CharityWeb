package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.InteractionRequest;
import group3.project.charityweb.model.dto.response.InteractionResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.model.enums.InteractionType;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.ActivityInteractionRepository;
import group3.project.charityweb.repository.ProjectActivityRepository;
import group3.project.charityweb.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final ActivityInteractionRepository interactionRepository;
    private final ProjectActivityRepository activityRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public String createInteraction(String username, String activityId, InteractionRequest request) {
        ProjectActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng cập nhật!"));

        User user = (User) accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không hợp lệ"));

        if (request.getType() == InteractionType.LIKE) {
            Long deletedCount = interactionRepository.deleteByActivity_ActivityIdAndUser_IdAndType(
                    activityId,
                    user.getId(),
                    InteractionType.LIKE
            );
            if (deletedCount != null && deletedCount > 0) {
                return null;
            }

            ActivityInteraction newLike = new ActivityInteraction();
            newLike.setActivity(activity);
            newLike.setUser(user);
            newLike.setType(InteractionType.LIKE);
            newLike.setCreatedAt(LocalDateTime.now());
            interactionRepository.save(newLike);
            return newLike.getInteractionId();
        }

        ActivityInteraction interaction = new ActivityInteraction();
        interaction.setActivity(activity);
        interaction.setUser(user);
        interaction.setType(request.getType());
        interaction.setContent(request.getType() == InteractionType.COMMENT ? request.getContent() : null); // Nếu là Like thì không cần content
        interaction.setCreatedAt(LocalDateTime.now());

        interactionRepository.save(interaction);

        return interaction.getInteractionId();
    }

    @Override
    public Page<InteractionResponse> getActivityInteractions(String activityId, int page, int size) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResourceNotFoundException("Bài đăng cập nhật không tồn tại!");
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ActivityInteraction> interactions = interactionRepository.findByActivity_ActivityIdOrderByCreatedAtDesc(activityId, pageRequest);

        return interactions.map(interaction -> {
            String displayName = "Người dùng ẩn danh";
            if (interaction.getUser() instanceof Individual ind) {
                displayName = ind.getFullName();
            } else if (interaction.getUser() instanceof Organization org) {
                displayName = org.getName();
            }

            return InteractionResponse.builder()
                    .interactionId(interaction.getInteractionId())
                    .type(interaction.getType())
                    .content(interaction.getContent())
                    .createdAt(interaction.getCreatedAt())
                    .userId(interaction.getUser().getId())
                    .username(interaction.getUser().getUsername())
                    .displayName(displayName)
                    .build();
        });
    }

    @Override
    @Transactional
    public void deleteInteraction(String username, String interactionId) {
        ActivityInteraction interaction = interactionRepository.findById(interactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tương tác/bình luận!"));

        if (!interaction.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Bạn không có quyền xóa bình luận của người khác!");
        }

        interactionRepository.delete(interaction);
    }
}