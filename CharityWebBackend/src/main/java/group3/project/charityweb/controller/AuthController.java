package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.LoginRequest;
import group3.project.charityweb.model.dto.request.RegisterIndivRequest;
import group3.project.charityweb.model.dto.request.RegisterOrgRequest;
import group3.project.charityweb.service.AuthService;
import group3.project.charityweb.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @PostMapping("/register/individual")
    public ResponseEntity<?> registerIndividual(@RequestBody RegisterIndivRequest request) {
        String newIndId = authService.registerIndividual(request); //
        return ResponseEntity.status(HttpStatus.CREATED) //
                .body(Map.of(
                        "message", "Đăng ký tài khoản Cá nhân thành công.", //
                        "id", newIndId //
                ));
    }

    @PostMapping("/register/organization")
    public ResponseEntity<?> registerOrganization(@RequestBody RegisterOrgRequest request) {
        String newOrgId = authService.registerOrganization(request); //
        return ResponseEntity.status(HttpStatus.CREATED) //
                .body(Map.of(
                        "message", "Đăng ký Tổ chức thành công. Vui lòng chờ Admin phê duyệt.", //
                        "id", newOrgId //
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Map<String, Object> authData = authService.authenticate(request);

        String accessToken = (String) authData.get("accessToken");
        String refreshToken = (String) authData.get("refreshToken");
        Object userProfile = authData.get("userProfile");

        ResponseCookie refreshCookie = cookieUtils.createRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "message", "Đăng nhập thành công!",
                        "user", userProfile,
                        "accessToken", accessToken
                ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_jwt", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Không tìm thấy Refresh Token. Vui lòng đăng nhập lại."));
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(Map.of(
                "message", "Đã làm mới token thành công.",
                "accessToken", newAccessToken
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cleanCookie = cookieUtils.getCleanRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "Đã đăng xuất thành công. Trình duyệt đã xóa Cookie."));
    }
}