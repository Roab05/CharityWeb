package group3.project.charityweb.chatbot.service;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.config.ChatbotProperties;
import group3.project.charityweb.chatbot.dto.response.ChatSource;
import group3.project.charityweb.chatbot.service.model.ChatContext;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.ProjectActivity;
import group3.project.charityweb.model.enums.ProjectStatus;
import group3.project.charityweb.repository.ProjectActivityRepository;
import group3.project.charityweb.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContextBuilderService {

    private final ProjectRepository projectRepository;
    private final ProjectActivityRepository projectActivityRepository;
    private final ChatbotProperties chatbotProperties;
    private final VectorSearchService vectorSearchService;
    private final GeminiClient geminiClient;

    public ChatContext build(String projectId, String userQuestion) {
        StringBuilder context = new StringBuilder();
        List<ChatSource> sources = new ArrayList<>();

        appendDonateFlow(context);

        if (projectId != null && !projectId.isBlank()) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay du an."));
            appendProject(context, sources, project);
            appendActivities(context, sources, project.getProjectId());
        } else {
            List<Float> questionVector = geminiClient.getEmbedding(userQuestion);

            // 2. Tìm top ID dự án liên quan nhất từ RAM
            List<String> similarIds = vectorSearchService.findTopSimilarProjectIds(questionVector, chatbotProperties.getMaxContextProjects());

            // 3. Kéo data thật từ Database (Cần thêm hàm findAllById trong Repo)
            List<Project> topProjects = projectRepository.findAllById(similarIds);

            for (Project project : topProjects) {
                appendProject(context, sources, project);
            }
        }

        return ChatContext.builder()
                .text(context.toString())
                .sources(sources)
                .build();
    }

    private void appendDonateFlow(StringBuilder context) {
        context.append("[DONATE_FLOW]\n")
                .append("1) Chon du an va tao donation.\n")
                .append("2) He thong tao URL thanh toan (VNPay).\n")
                .append("3) Nguoi dung thanh toan tai URL, sau do VNPay goi callback ve server.\n")
                .append("4) Sau callback thanh cong, donation duoc cap nhat SUCCESS va cong tien cho du an.\n")
                .append("5) Nguoi dung co the xem lich su luot ung ho tai users/me/donations, va lich su giao dich thanh toan tai users/me/transactions.\n\n");
    }

    private void appendProject(StringBuilder context, List<ChatSource> sources, Project project) {
        String organizations = project.getOrganizations().stream()
                .map(org -> defaultText(org.getName())) // Giả sử phương thức lấy tên là getOrgName()
                .collect(Collectors.joining(", "));

        context.append("[PROJECT]")
                .append(" id=").append(project.getProjectId())
                .append("; name=").append(defaultText(project.getProjectName()))
                .append("; organizationName=").append(organizations)
                .append("; status=").append(project.getStatus())
                .append("; target=").append(project.getTargetAmount())
                .append("; current=").append(project.getCurrentAmount())
                .append("; endDate=").append(project.getEndDate())
                .append("; description=").append(truncate(defaultText(project.getDescription()), chatbotProperties.getMaxProjectDescriptionChars()))
                .append("\n");

        sources.add(ChatSource.builder()
                .type("PROJECT")
                .id(project.getProjectId())
                .title(defaultText(project.getProjectName()))
                .build());
    }

    private void appendActivities(StringBuilder context, List<ChatSource> sources, String projectId) {
        PageRequest pageRequest = PageRequest.of(0, chatbotProperties.getMaxContextActivities());
        List<ProjectActivity> activities = projectActivityRepository.findByProject_ProjectId(projectId, pageRequest).getContent();
        for (ProjectActivity activity : activities) {
            context.append("[ACTIVITY]")
                    .append(" id=").append(activity.getActivityId())
                    .append("; title=").append(defaultText(activity.getTitle()))
                    .append("; content=").append(truncate(defaultText(activity.getContent()), chatbotProperties.getMaxActivityContentChars()))
                    .append("\n");

            sources.add(ChatSource.builder()
                    .type("ACTIVITY")
                    .id(activity.getActivityId())
                    .title(defaultText(activity.getTitle()))
                    .build());
        }
    }

    private String defaultText(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value.replace("\n", " ").trim();
    }

    private String truncate(String value, int maxChars) {
        if (value == null || value.length() <= maxChars || maxChars <= 0) {
            return value;
        }
        return value.substring(0, maxChars) + "...";
    }
}



