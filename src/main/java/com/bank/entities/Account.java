package com.bank.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "accounts")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private String id;
    private String accountNumber;
    public static final String SEQUENCE_NAME = "account_sequence";
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String userId;
    private BigDecimal balance;
    private AccountType accountType;

    // Constructors, getters, and setters
}
