package group3.project.charityweb.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {
    private int status;           // Mã HTTP (VD: 400, 404, 500)
    private String error;         // Tên lỗi ngắn gọn (VD: Not Found, Bad Request)
    private String message;       // Câu thông báo chi tiết cho người dùng
    private String path;          // API Endpoint nào đang bị lỗi
    private LocalDateTime timestamp; // Thời gian xảy ra lỗi
}