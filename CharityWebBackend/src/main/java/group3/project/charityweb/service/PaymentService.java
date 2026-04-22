package group3.project.charityweb.service;

import group3.project.charityweb.model.entity.Transaction;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String createPaymentUrl(Transaction transaction, HttpServletRequest request);
    String processCallback(HttpServletRequest request);
}