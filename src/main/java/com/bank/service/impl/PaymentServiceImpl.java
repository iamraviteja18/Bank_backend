package com.bank.service.impl;

import com.bank.entities.Account;
import com.bank.entities.Payment;
import com.bank.entities.PaymentRequest;
import com.bank.entities.Refund;
import com.bank.repository.AccountRepository;
import com.bank.repository.PaymentRepository;
import com.bank.repository.RefundRepository;
import com.bank.service.PaymentService;
import com.mongodb.client.MongoIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Override
    public Payment processPayment(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Payment payment = new Payment();
        payment.setAccountNumber(accountNumber);
        payment.setAmount(amount);
        payment.setTimestamp(Instant.now());
        paymentRepository.save(payment);

        return payment;
    }


    @Override
    public Refund processRefund(String paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isEmpty()) {
            throw new IllegalArgumentException("Payment not found.");
        }
        Account account = accountRepository.findByAccountNumber(payment.get().getAccountNumber());
        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        account.setBalance(account.getBalance().add(payment.get().getAmount()));
        accountRepository.save(account);

        Refund refund = new Refund();
        refund.setPaymentId(paymentId);
        refund.setAmount(payment.get().getAmount());
        refund.setTimestamp(Instant.now());
        refundRepository.save(refund);
        return refund;
    }

    @Override
    public MongoIterable<Object> initiatePayment(String username, PaymentRequest request) {
        return null;
    }
}
