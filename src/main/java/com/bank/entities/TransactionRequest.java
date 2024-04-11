package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document("transactions")
public class TransactionRequest {
    @Id
    private String id;
    private String toUserId;
    private String fromUserId;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
    private LocalDateTime transactionTime;
    private PaymentStatus status;
    // Getters and Setters
}
