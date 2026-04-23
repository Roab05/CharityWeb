package group3.project.charityweb.controller;

import group3.project.charityweb.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${app.payment-result-url:http://localhost:3000/payment/result}")
    private String paymentResultUrl;

        String queryString = request.getQueryString();

        String frontendUrl = "http://localhost:3000/payment/result?" + queryString;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendUrl))
                .build();
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.processIpnCallback(request));
    }

    private ResponseEntity<Void> redirectToFrontendResult(HttpServletRequest request, String frontendError) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(paymentResultUrl)
                .queryParam("vnp_ResponseCode", safeParam(request, "vnp_ResponseCode"))
                .queryParam("vnp_TransactionStatus", safeParam(request, "vnp_TransactionStatus"))
                .queryParam("vnp_TxnRef", safeParam(request, "vnp_TxnRef"))
                .queryParam("vnp_Amount", safeParam(request, "vnp_Amount"));

        if (frontendError != null) {
            builder.queryParam("frontend_error", frontendError);
        }

        URI location = builder.build(true).toUri();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, location.toString())
                .build();
    }

    private String safeParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }
}