package group3.project.charityweb.controller;

import group3.project.charityweb.model.dto.request.LoginRequest;
import group3.project.charityweb.model.dto.request.RegisterIndivRequest;
import group3.project.charityweb.model.dto.request.RegisterOrgRequest;
import group3.project.charityweb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/individual")
    public ResponseEntity<?> registerIndividual(@RequestBody RegisterIndivRequest request) {
        authService.registerIndividual(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Đăng ký tài khoản cá nhân thành công"));
    }

    @PostMapping("/register/organization")
    public ResponseEntity<?> registerOrganization(@RequestBody RegisterOrgRequest request) {
        authService.registerOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Đăng ký tài khoản tổ chức thành công"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.authenticate(request);
        return ResponseEntity.ok(Map.of(
                "message", "Đăng nhập thành công",
                "accessToken", token
        ));
    }
}