package group3.project.charityweb.model.enums;

public enum DonationStatus {
    PROCESSING, // Đang chờ người dùng quét mã/thanh toán trên VNPAY
    SUCCESS,    // Thanh toán thành công, tiền đã vào tài khoản
    FAILED      // Thanh toán thất bại hoặc người dùng hủy giao dịch
}