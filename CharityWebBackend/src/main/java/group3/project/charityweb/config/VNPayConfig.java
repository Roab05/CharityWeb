package group3.project.charityweb.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class VNPayConfig {
    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.url}")
    private String vnpUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpReturnUrl;

    @Value("${vnpay.ipnUrl:http://localhost:8080/api/v1/payments/ipn}")
    private String vnpIpnUrl;

    @Value("${payment.pending-timeout-minutes:20}")
    private long pendingTimeoutMinutes;

    public String getVnpVersion() { return "2.1.0"; }
    public String getVnpCommand() { return "pay"; }
}