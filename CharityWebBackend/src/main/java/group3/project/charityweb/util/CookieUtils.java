package group3.project.charityweb.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    @Value("${app.jwt.refreshExpiration}") // 👈 SỬ DỤNG HẠN CỦA REFRESH TOKEN
    private int refreshExpirationMs;

    // 1. Tạo Cookie chứa Refresh Token
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_jwt", refreshToken)
                .httpOnly(true)
                .secure(false) // Đổi thành true khi đem lên server thực tế chạy HTTPS
                .path("/api/v1/auth/refresh-token") // BẢO MẬT: Chỉ cho phép gửi lên API cấp lại Token
                .maxAge(refreshExpirationMs / 1000)
                .sameSite("Lax")
                .build();
    }

    // 2. Tạo Cookie rỗng để dọn dẹp khi đăng xuất
    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from("refresh_jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth/refresh-token")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}