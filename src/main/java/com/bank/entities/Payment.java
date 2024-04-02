package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document
@Getter
@Setter
public class Payment {
    @Id
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private Instant timestamp;
    // Other fields and methods
}

