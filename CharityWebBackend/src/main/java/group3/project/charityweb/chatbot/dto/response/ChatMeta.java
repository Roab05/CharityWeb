package group3.project.charityweb.chatbot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMeta {
    private String model;
    private long latencyMs;
    private long contextBuildLatencyMs;
    private long modelCallLatencyMs;
    private boolean fallbackUsed;
}


