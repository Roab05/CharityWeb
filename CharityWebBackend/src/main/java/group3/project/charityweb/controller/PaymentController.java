package group3.project.charityweb.controller;

import group3.project.charityweb.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // GET /api/v1/payments/callback
    @GetMapping("/callback")
    public ResponseEntity<?> vnpayCallback(HttpServletRequest request) {
        paymentService.processCallback(request);

        return ResponseEntity.ok(Map.of(
                "message", "Giao dịch đã được xử lý thành công. Bạn có thể đóng cửa sổ này."
        ));
    }
}