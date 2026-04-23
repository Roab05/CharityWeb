package group3.project.charityweb.chatbot.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    public List<String> getSuggestions(String projectId) {
        List<String> suggestions = new ArrayList<>();
        if (projectId != null && !projectId.isBlank()) {
            suggestions.add("Dự án này hiện ở giai đoạn nào?");
            suggestions.add("Tổng số tiền dự án đã nhận ủng hộ là bao nhiêu?");
            suggestions.add("Cách quyên góp cho dự án này là thế nào?");
            suggestions.add("Dự án này có những cập nhật tiến độ nào gần đây?");
            return suggestions;
        }

        suggestions.add("Tôi có thể quyên góp cho dự án bằng cách nào?");
        suggestions.add("Làm sao để xem các dự án đang nhận quyên góp?");
        suggestions.add("Sau khi thanh toán thành công, hệ thống xử lý trang thái ủng hộ như nào?");
        suggestions.add("Tôi có thể xem lịch sử ủng hộ và giao dịch của mình ở đâu?");
        return suggestions;
    }
}

