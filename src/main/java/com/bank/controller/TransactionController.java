package com.bank.controller;

import com.bank.entities.*;
import com.bank.service.AccountService;
import com.bank.service.PaymentService;
import com.bank.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/processPayment")
    public ResponseEntity<Payment> processPayment(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        try {
            Payment payment = paymentService.processPayment(accountNumber, amount);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Consider a more descriptive error handling
        }
    }

    @PostMapping("/processRefund")
    public ResponseEntity<Refund> processRefund(@RequestParam String paymentId) {
        try {
            Refund refund = paymentService.processRefund(paymentId);
            return ResponseEntity.ok(refund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Consider a more descriptive error handling
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debitFunds(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.debit(transactionRequest.getFromAccountId(), transactionRequest.getAmount());
    }

    @PostMapping("/credit")
    public ResponseEntity<?> creditFunds(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.credit(transactionRequest.getToAccountId(), transactionRequest.getAmount());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.transferFunds(transactionRequest.getFromAccountId(),transactionRequest.getToAccountId(), transactionRequest.getAmount());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionRequest>> listPendingTransactions() {
        List<TransactionRequest> pendingTransactions = transactionService.findAllPendingTransactions();
        return ResponseEntity.ok(pendingTransactions);
    }

    @PostMapping("/approveTransaction")
    public ResponseEntity<String> approveTransaction(@RequestBody ApprovalRequest approvalRequest) {
        try {
            // Step 1: Fetch the transaction and attempt to approve/decline it
            String status = transactionService.approveTransaction(approvalRequest.getTransactionId(), approvalRequest.isApprove());
            return ResponseEntity.ok(status);
        } catch (IllegalStateException e) {
            // Handle specific known exceptions with appropriate messages
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // General exception handling, could be due to unexpected errors
            // Consider logging this error as well
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


}
