package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Document("transactions")
@Getter
@Setter
public class Transaction {
    private String userId;
    private String accountId;
    private TransactionType transactionType; // Enum for DEBIT, CREDIT, PAYMENT, REFUND
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime transactionTime;
    private PaymentStatus status;
    // Other fields and methods
}
