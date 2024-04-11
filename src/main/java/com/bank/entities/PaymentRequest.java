package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentRequest {
        private String paymentMethodId;
        private Long amount;
        private String accountNumber;
        private String routingNumber;
        private String customerEmail;
        private String clientIp;
        private String clientUserAgent;
        // Getters and Setters
    }