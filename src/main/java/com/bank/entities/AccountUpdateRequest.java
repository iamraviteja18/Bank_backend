package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountUpdateRequest {
    private String accountId;
    private BigDecimal balance;
    // Add other fields that can be updated, like account type if applicable

    public AccountUpdateRequest() {
    }

    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // Consider adding constructors, additional fields, and methods as necessary
}
