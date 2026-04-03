package group3.project.charityweb.service.impl;

import group3.project.charityweb.config.VNPayConfig;
import group3.project.charityweb.exception.PaymentFailedException;
import group3.project.charityweb.exception.PaymentUrlGenerationException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.model.entity.Donation;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.entity.Transaction;
import group3.project.charityweb.model.entity.User;
import group3.project.charityweb.repository.DonationRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.repository.TransactionRepository;
import group3.project.charityweb.repository.UserRepository;
import group3.project.charityweb.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.getVnpVersion());
        vnp_Params.put("vnp_Command", vnPayConfig.getVnpCommand());
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", transaction.getTransactionId()); // Dùng ID làm mã tham chiếu
        vnp_Params.put("vnp_OrderInfo", "Quyen gop du an: " + transaction.getDonation().getProject().getProjectId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1"); // Nên lấy thực tế từ request.getRemoteAddr()

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Build string & hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (fieldNames.indexOf(fieldName) != fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            return vnPayConfig.getVnpUrl() + "?" + queryUrl;
        } catch (Exception e) {
            throw new PaymentUrlGenerationException("Lỗi thuật toán khi tạo URL VNPAY: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processCallback(HttpServletRequest request) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef"); // Đây chính là Transaction ID
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo"); // Mã của VNPAY

        Transaction transaction = transactionRepository.findById(vnp_TxnRef)
                .orElseThrow(() -> new ResourceNotFoundException("Giao dịch không tồn tại!"));

        // Check mã 00 = Thành công
        if ("00".equals(vnp_ResponseCode)) {
            transaction.setPaymentStatus(1); // 1 = Success
            transaction.setCompletedAt(LocalDateTime.now());
            transaction.setGatewayTransactionNo(vnp_TransactionNo);

            Donation donation = transaction.getDonation();
            donation.setStatus(1); // 1 = Thành công

            // Nghiệp vụ cốt lõi: Đồng bộ cộng tiền
            Project project = donation.getProject();
            project.setCurrentAmount(project.getCurrentAmount().add(donation.getAmount()));

            User user = donation.getUser();
            user.setTotalDonatedAmount(user.getTotalDonatedAmount().add(donation.getAmount()));

            projectRepository.save(project);
            userRepository.save(user);
            donationRepository.save(donation);
            transactionRepository.save(transaction);
        } else {
            transaction.setPaymentStatus(0);
            transaction.getDonation().setStatus(0);

            transactionRepository.save(transaction);

            throw new PaymentFailedException("Giao dịch bị hủy hoặc thanh toán thất bại từ phía VNPAY (Mã lỗi: " + vnp_ResponseCode + ")");
        }
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