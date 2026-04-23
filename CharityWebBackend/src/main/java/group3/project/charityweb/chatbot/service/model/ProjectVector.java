package group3.project.charityweb.chatbot.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ProjectVector {
    private String projectId;
    private List<Float> vector;
}