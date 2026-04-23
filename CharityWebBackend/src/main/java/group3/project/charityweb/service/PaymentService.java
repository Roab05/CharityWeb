package group3.project.charityweb.service;

import group3.project.charityweb.model.entity.Transaction;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(Transaction transaction, HttpServletRequest request);
    void processReturnCallback(HttpServletRequest request);
    Map<String, String> processIpnCallback(HttpServletRequest request);
}