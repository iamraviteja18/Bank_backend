package com.bank.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequest {
    private boolean approve;
    private String transactionId;
}
