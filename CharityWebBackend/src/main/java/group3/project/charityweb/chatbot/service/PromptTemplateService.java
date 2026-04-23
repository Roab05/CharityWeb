package group3.project.charityweb.chatbot.service;

import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    public String buildSystemPrompt(String locale) {
        String language = locale == null || locale.isBlank() ? "vi-VN" : locale;
        return "Ban la tro ly AI cua he thong CharityWeb. " +
                "Chi tra loi dua tren du lieu duoc cung cap trong context. " +
                "Khong suy dien, khong bịa so lieu, khong dua loi khuyen tai chinh/phap ly. " +
                "Neu context khong du, hay noi ro va goi y nguoi dung xem trang du an hoac lien he to chuc. " +
                "Ngon ngu tra loi: " + language + ". Tra loi ngan gon, day du, theo cac buoc ro rang, de hieu, uu tien bullet khi can.";
    }

    public String buildUserPrompt(String question, String contextText) {
        return "Cau hoi nguoi dung:\n" + question + "\n\n" +
                "Context he thong:\n" + contextText + "\n\n" +
                "Yeu cau: tra loi dung nghiep vu quyen gop, neu thieu thong tin thi noi ro thieu o dau.";
    }

    public String buildContinuationPrompt(String question, String contextText, String partialAnswer) {
        return "Cau hoi nguoi dung:\n" + question + "\n\n" +
                "Context he thong:\n" + contextText + "\n\n" +
                "Cau tra loi dang bi do dang:\n" + partialAnswer + "\n\n" +
                "Yeu cau: hoan thanh cau tra loi dang do theo cac buoc so thu tu (1,2,3...), " +
                "khong lap lai nguyen van phan da viet, va ket thuc bang cau day du.";
    }
}


