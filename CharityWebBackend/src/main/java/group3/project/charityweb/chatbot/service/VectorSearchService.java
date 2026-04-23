package group3.project.charityweb.chatbot.service;

import group3.project.charityweb.chatbot.client.GeminiClient;
import group3.project.charityweb.chatbot.service.model.ProjectVector;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.enums.ProjectStatus;
import group3.project.charityweb.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final ProjectRepository projectRepository;
    private final GeminiClient geminiClient;

    // Nơi lưu trữ Vector trên RAM
    private final List<ProjectVector> inMemoryStore = new ArrayList<>();

    // Chạy 1 lần duy nhất khi App khởi động xong
    @EventListener(ApplicationReadyEvent.class)
    public void initVectorStore() {
        log.info("[Vector Store] Đang khởi tạo bộ nhớ ngữ nghĩa...");
        List<Project> activeProjects = projectRepository.findByStatusOrderByCreatedAtDesc(ProjectStatus.ACTIVE); // Bạn cần thêm hàm này trong Repo

        for (Project project : activeProjects) {
            // Ghép nối nội dung cần AI "hiểu"
            String contentToEmbed = project.getProjectName() + ". " + project.getDescription();
            try {
                List<Float> vector = geminiClient.getEmbedding(contentToEmbed);
                inMemoryStore.add(new ProjectVector(project.getProjectId(), vector));
            } catch (Exception e) {
                log.warn("[Vector Store] Lỗi embedding dự án {}: {}", project.getProjectId(), e.getMessage());
            }
        }
        log.info("[Vector Store] Khởi tạo xong. Đã lưu {} dự án lên RAM.", inMemoryStore.size());
    }

    // Hàm tìm kiếm dự án tương đồng nhất
    public List<String> findTopSimilarProjectIds(List<Float> queryVector, int topK) {
        if (inMemoryStore.isEmpty() || queryVector == null) {
            return new ArrayList<>();
        }

        return inMemoryStore.stream()
                .sorted(Comparator.comparingDouble((ProjectVector pv) -> cosineSimilarity(queryVector, pv.getVector())).reversed())
                .limit(topK)
                .map(ProjectVector::getProjectId)
                .collect(Collectors.toList());
    }

    // Thuật toán toán học: Cosine Similarity (Càng gần 1.0 càng giống nhau)
    private double cosineSimilarity(List<Float> vectorA, List<Float> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}