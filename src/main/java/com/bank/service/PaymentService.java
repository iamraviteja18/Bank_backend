package com.bank.service;

import com.bank.entities.Payment;
import com.bank.entities.PaymentRequest;
import com.bank.entities.Refund;
import com.mongodb.client.MongoIterable;

import java.math.BigDecimal;

public interface PaymentService {
    Payment processPayment(String accountId, BigDecimal amount);
    Refund processRefund(String paymentId);

    MongoIterable<Object> initiatePayment(String username, PaymentRequest request);
}
