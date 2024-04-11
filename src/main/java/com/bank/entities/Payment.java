package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
//public class Payment {
//    @Id
//    private String id;
//    private String accountNumber;
//    private BigDecimal amount;
//    private Instant timestamp;
//    // Other fields and methods
//}
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String stripePaymentIntentId;
    private String accountNumber;
    private BigDecimal amount;
    private Instant timestamp;
    private String status;
    private String currency;

    // Constructors, Getters, and Setters
}

