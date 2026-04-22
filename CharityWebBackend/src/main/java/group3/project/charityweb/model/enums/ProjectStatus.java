package group3.project.charityweb.model.enums;

public enum ProjectStatus {
    PENDING,    // Chờ Admin duyệt
    ACTIVE,     // Đang kêu gọi quyên góp
    COMPLETED,  // Đã đạt mục tiêu / Đóng dự án
    REJECTED,   // Bị Admin từ chối lúc duyệt
    SUSPENDED   // Bị Admin đình chỉ giữa chừng do vi phạm
}