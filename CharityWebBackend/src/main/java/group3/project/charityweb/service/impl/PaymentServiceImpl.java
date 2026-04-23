package group3.project.charityweb.service.impl;

import group3.project.charityweb.config.VNPayConfig;
import group3.project.charityweb.exception.PaymentFailedException;
import group3.project.charityweb.exception.PaymentUrlGenerationException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.entity.Donation;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.Transaction;
import group3.project.charityweb.model.entity.User;
import group3.project.charityweb.model.enums.DonationStatus;
import group3.project.charityweb.model.enums.TransactionStatus;
import group3.project.charityweb.repository.DonationRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.repository.TransactionRepository;
import group3.project.charityweb.repository.UserRepository;
import group3.project.charityweb.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final TransactionRepository transactionRepository;
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public String createPaymentUrl(Transaction transaction, HttpServletRequest request) {
        long amount = transaction.getAmount().longValue() * 100;

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayConfig.getVnpVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getVnpCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", transaction.getTransactionId());
        vnpParams.put("vnp_OrderInfo", "Quyen gop du an: " + transaction.getDonation().getProject().getProjectId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnpParams.put("vnp_IpAddr", resolveClientIp(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnpParams.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        try {
            String hashData = buildSignData(vnpParams);
            String query = buildQuery(vnpParams);
            String secureHash = hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData);
            return vnPayConfig.getVnpUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
        } catch (Exception e) {
            throw new PaymentUrlGenerationException("Lỗi thuật toán khi tạo URL VNPAY: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processReturnCallback(HttpServletRequest request) {
        if (!isValidSignature(request)) {
            throw new PaymentFailedException("Chu ky callback khong hop le.");
        }
        applyGatewayResult(request);
    }

    @Override
    @Transactional
    public Map<String, String> processIpnCallback(HttpServletRequest request) {
        if (!isValidSignature(request)) {
            return Map.of("RspCode", "97", "Message", "Invalid signature");
        }

        String txnRef = request.getParameter("vnp_TxnRef");
        Transaction transaction = transactionRepository.findById(txnRef).orElse(null);

        if (transaction == null) {
            return Map.of("RspCode", "01", "Message", "Order not found");
        }

        if (transaction.getPaymentStatus() != TransactionStatus.PENDING) {
            return Map.of("RspCode", "02", "Message", "Order already confirmed");
        }

        applyGatewayResult(request);
        return Map.of("RspCode", "00", "Message", "Confirm Success");
    }

    @Scheduled(fixedDelayString = "${payment.timeout-job-fixed-delay-ms:60000}")
    @Transactional
    public void timeoutPendingTransactions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(vnPayConfig.getPendingTimeoutMinutes());
        List<Transaction> expired = transactionRepository.findByPaymentStatusAndCreatedAtBefore(TransactionStatus.PENDING, cutoff);

        for (Transaction transaction : expired) {
            markFailed(transaction);
        }
    }

    private void applyGatewayResult(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        String txnRef = request.getParameter("vnp_TxnRef");
        String gatewayTxnNo = request.getParameter("vnp_TransactionNo");

        Transaction transaction = transactionRepository.findById(txnRef)
                .orElseThrow(() -> new ResourceNotFoundException("Giao dich khong ton tai!"));

        if (transaction.getPaymentStatus() == TransactionStatus.SUCCESS) {
            return;
        }

        boolean success = "00".equals(responseCode)
                && (transactionStatus == null || "00".equals(transactionStatus));

        if (success) {
            markSuccess(transaction, gatewayTxnNo);
            return;
        }

        markFailed(transaction);
    }

    private void markSuccess(Transaction transaction, String gatewayTxnNo) {
        if (transaction.getPaymentStatus() == TransactionStatus.SUCCESS) {
            return;
        }

        Donation donation = transaction.getDonation();
        if (donation.getStatus() != DonationStatus.SUCCESS) {
            Project project = donation.getProject();
            project.setCurrentAmount(project.getCurrentAmount().add(donation.getAmount()));

            User user = donation.getUser();
            user.setTotalDonatedAmount(user.getTotalDonatedAmount().add(donation.getAmount()));

            donation.setStatus(DonationStatus.SUCCESS);
            projectRepository.save(project);
            userRepository.save(user);
            donationRepository.save(donation);
        }

        transaction.setPaymentStatus(TransactionStatus.SUCCESS);
        transaction.setGatewayTransactionNo(gatewayTxnNo);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private void markFailed(Transaction transaction) {
        transaction.setPaymentStatus(TransactionStatus.FAILED);
        transaction.setCompletedAt(LocalDateTime.now());

        Donation donation = transaction.getDonation();
        donation.setStatus(DonationStatus.FAILED);

        donationRepository.save(donation);
        transactionRepository.save(transaction);
    }

    private boolean isValidSignature(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0 && v[0] != null && !v[0].isEmpty()) {
                fields.put(k, v[0]);
            }
        });

        String secureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        if (secureHash == null || secureHash.isEmpty()) {
            return false;
        }

        try {
            String signData = buildSignData(fields);
            String expected = hmacSHA512(vnPayConfig.getVnpHashSecret(), signData);
            return secureHash.equalsIgnoreCase(expected);
        } catch (Exception e) {
            return false;
        }
    }

    private String buildSignData(Map<String, String> params) {
        List<String> fieldNames = params.keySet().stream().sorted().toList();
        StringBuilder hashData = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);
            hashData.append(fieldName)
                    .append('=')
                    .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
            }
        }
        return hashData.toString();
    }

    private String buildQuery(Map<String, String> params) {
        List<String> fieldNames = params.keySet().stream().sorted().toList();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);
            query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                    .append('=')
                    .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            if (i < fieldNames.size() - 1) {
                query.append('&');
            }
        }
        return query.toString();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // Hàm Hash SHA-512 chuẩn của VNPAY
    private String hmacSHA512(final String key, final String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(2 * result.length);
        for (byte b : result) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}