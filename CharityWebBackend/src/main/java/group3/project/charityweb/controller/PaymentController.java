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

    @GetMapping("/callback")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request) {
        paymentService.processReturnCallback(request);

        return ResponseEntity.ok(Map.of(
                "message", "Da ghi nhan ket qua giao dich."
        ));
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.processIpnCallback(request));
    }
}