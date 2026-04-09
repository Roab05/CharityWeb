package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.InteractionRequest;
import group3.project.charityweb.model.dto.response.InteractionResponse;
import group3.project.charityweb.model.entity.*;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.ActivityInteractionRepository;
import group3.project.charityweb.repository.ProjectActivityRepository;
import group3.project.charityweb.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        ActivityInteraction interaction = new ActivityInteraction();
        interaction.setActivity(activity);
        interaction.setUser(user);
        interaction.setType(request.getType());
        interaction.setContent(request.getType() == 1 ? request.getContent() : null); // Nếu là Like (2) thì không cần content
        interaction.setCreatedAt(LocalDateTime.now());

        interactionRepository.save(interaction);

        return interaction.getInteractionId();
    }

    @Override
    public List<InteractionResponse> getActivityInteractions(String activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResourceNotFoundException("Bài đăng cập nhật không tồn tại!");
        }

        List<ActivityInteraction> interactions = interactionRepository.findByActivity_ActivityIdOrderByCreatedAtDesc(activityId);

        return interactions.stream().map(interaction -> {
            // Lấy tên hiển thị dựa vào loại User
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
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteInteraction(String username, String interactionId) {
        ActivityInteraction interaction = interactionRepository.findById(interactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tương tác/bình luận!"));

        // CỰC KỲ QUAN TRỌNG: Chỉ cho phép xóa nếu người đang request chính là chủ nhân của bình luận
        if (!interaction.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Bạn không có quyền xóa bình luận của người khác!");
        }

        interactionRepository.delete(interaction);
    }
}