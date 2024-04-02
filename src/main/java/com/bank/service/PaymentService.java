package com.bank.service;

import com.bank.entities.Payment;
import com.bank.entities.Refund;

import java.math.BigDecimal;

public interface PaymentService {
    Payment processPayment(String accountId, BigDecimal amount);
    Refund processRefund(String paymentId);
}
