package com.bank.service;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface TransactionService {
    ResponseEntity<?> debit(String accountId, BigDecimal amount);
    ResponseEntity<?> credit(String accountId, BigDecimal amount);

    ResponseEntity<?> transfer(String accountId, BigDecimal amount);
    public ResponseEntity<?> transferFunds(String fromAccountId, String toAccountId, BigDecimal amount);
}
