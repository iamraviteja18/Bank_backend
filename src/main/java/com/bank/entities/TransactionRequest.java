package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Document("transactions")
public class TransactionRequest {
    private String userId;
    private String accountId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
    // Getters and Setters
}
