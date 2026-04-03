package group3.project.charityweb.model.dto.request;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private Integer status; // 1: Duyệt/Mở khóa, 0: Từ chối/Khóa
}