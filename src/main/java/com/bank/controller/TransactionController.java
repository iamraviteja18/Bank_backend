package com.bank.controller;

import com.bank.entities.TransactionRequest;
import com.bank.service.AccountService;
import com.bank.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/debit")
    public ResponseEntity<?> debitFunds(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.debit(transactionRequest.getAccountId(), transactionRequest.getAmount());
    }

    @PostMapping("/credit")
    public ResponseEntity<?> creditFunds(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.credit(transactionRequest.getAccountId(), transactionRequest.getAmount());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.transfer(transactionRequest.getAccountId(), transactionRequest.getAmount());
    }
}
