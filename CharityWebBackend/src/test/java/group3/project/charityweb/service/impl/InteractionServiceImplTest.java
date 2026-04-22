package group3.project.charityweb.service.impl;

import group3.project.charityweb.model.dto.request.InteractionRequest;
import group3.project.charityweb.model.entity.ActivityInteraction;
import group3.project.charityweb.model.entity.ProjectActivity;
import group3.project.charityweb.model.entity.User;
import group3.project.charityweb.model.enums.InteractionType;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.ActivityInteractionRepository;
import group3.project.charityweb.repository.ProjectActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionServiceImplTest {

    @Mock
    private ActivityInteractionRepository interactionRepository;

    @Mock
    private ProjectActivityRepository activityRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private InteractionServiceImpl interactionService;

    private User user;
    private ProjectActivity activity;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-1");
        user.setUsername("demo");

        activity = new ProjectActivity();
        activity.setActivityId("activity-1");

        when(activityRepository.findById("activity-1")).thenReturn(Optional.of(activity));
        when(accountRepository.findByUsername("demo")).thenReturn(Optional.of(user));
    }

    @Test
    void createInteraction_likeWhenNotExists_shouldCreateLike() {
        InteractionRequest request = new InteractionRequest();
        request.setType(InteractionType.LIKE);

        when(interactionRepository.deleteByActivity_ActivityIdAndUser_IdAndType(
                "activity-1", "user-1", InteractionType.LIKE)).thenReturn(0L);

        when(interactionRepository.save(any(ActivityInteraction.class))).thenAnswer(invocation -> {
            ActivityInteraction saved = invocation.getArgument(0);
            saved.setInteractionId("like-1");
            return saved;
        });

        String interactionId = interactionService.createInteraction("demo", "activity-1", request);

        assertEquals("like-1", interactionId);

        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(interactionRepository).save(captor.capture());
        ActivityInteraction savedInteraction = captor.getValue();

        assertEquals(InteractionType.LIKE, savedInteraction.getType());
        assertNull(savedInteraction.getContent());
        assertEquals(user, savedInteraction.getUser());
        assertEquals(activity, savedInteraction.getActivity());
        assertNotNull(savedInteraction.getCreatedAt());
    }

    @Test
    void createInteraction_likeWhenExists_shouldUnlike() {
        InteractionRequest request = new InteractionRequest();
        request.setType(InteractionType.LIKE);

        when(interactionRepository.deleteByActivity_ActivityIdAndUser_IdAndType(
                "activity-1", "user-1", InteractionType.LIKE)).thenReturn(1L);

        String interactionId = interactionService.createInteraction("demo", "activity-1", request);

        assertNull(interactionId);
        verify(interactionRepository).deleteByActivity_ActivityIdAndUser_IdAndType(
                "activity-1", "user-1", InteractionType.LIKE);
        verify(interactionRepository, never()).save(any(ActivityInteraction.class));
    }

    @Test
    void createInteraction_comment_shouldAlwaysCreateNewComment() {
        InteractionRequest request = new InteractionRequest();
        request.setType(InteractionType.COMMENT);
        request.setContent("hello");

        when(interactionRepository.save(any(ActivityInteraction.class))).thenAnswer(invocation -> {
            ActivityInteraction saved = invocation.getArgument(0);
            saved.setInteractionId("comment-1");
            return saved;
        });

        String interactionId = interactionService.createInteraction("demo", "activity-1", request);

        assertEquals("comment-1", interactionId);

        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(interactionRepository).save(captor.capture());
        verify(interactionRepository, never()).deleteByActivity_ActivityIdAndUser_IdAndType(
                anyString(), anyString(), eq(InteractionType.LIKE));

        ActivityInteraction savedInteraction = captor.getValue();
        assertEquals(InteractionType.COMMENT, savedInteraction.getType());
        assertEquals("hello", savedInteraction.getContent());
    }
}


