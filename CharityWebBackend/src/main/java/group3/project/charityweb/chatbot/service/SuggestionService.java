package group3.project.charityweb.chatbot.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    public List<String> getSuggestions(String projectId) {
        List<String> suggestions = new ArrayList<>();
        if (projectId != null && !projectId.isBlank()) {
            suggestions.add("Du an nay hien dang o giai doan nao?");
            suggestions.add("Tong so tien da nhan cua du an la bao nhieu?");
            suggestions.add("Cach quyen gop cho du an nay nhu the nao?");
            suggestions.add("Du an da co nhung cap nhat tien do nao gan day?");
            return suggestions;
        }

        suggestions.add("Toi co the quyen gop cho du an bang cach nao?");
        suggestions.add("Lam sao de xem cac du an dang hoat dong?");
        suggestions.add("Sau khi thanh toan thanh cong, he thong xu ly don quyen gop nhu the nao?");
        suggestions.add("Toi co the xem lich su giao dich cua minh o dau?");
        return suggestions;
    }
}

