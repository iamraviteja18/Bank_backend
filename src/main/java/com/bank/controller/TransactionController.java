package com.bank.controller;

import com.bank.entities.*;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import com.bank.service.JwtService;
import com.bank.service.PaymentService;
import com.bank.service.impl.TransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

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
    public ResponseEntity<?> transfer(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            User usr = user.get();
            return transactionService.transferFunds(transactionRequest.getFromAccountId(),transactionRequest.getToAccountId(), transactionRequest.getAmount(),usr);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found or not authorized");
        }
//        return transactionService.transferFunds(transactionRequest.getFromAccountId(),transactionRequest.getToAccountId(), transactionRequest.getAmount());
    }

    @PostMapping("/requestFunds")
    public ResponseEntity<?> requestFunds(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            User usr = user.get();
            return transactionService.requestFunds(transactionRequest.getFromAccountId(),transactionRequest.getToAccountId(), transactionRequest.getAmount(),usr);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found or not authorized");
        }
//        return transactionService.requestFunds(transactionRequest.getFromAccountId(),transactionRequest.getToAccountId(), transactionRequest.getAmount());
    }

    @GetMapping("/adminpending")
    public ResponseEntity<List<TransactionRequest>> listAdminPendingTransactions() {
        List<TransactionRequest> pendingTransactions = transactionService.findAllPendingAdminTransactions();
        return ResponseEntity.ok(pendingTransactions);
    }

    @GetMapping("/customer")
    public ResponseEntity<?> listCustomerPendingTransactions(HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        logger.debug("username :::::::::::::::::::{}",username);
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isPresent()) {
            logger.debug("UserID : {}",user.get().getUserId());
//            return ResponseEntity.ok(accountService.listAccountsByUserId(user.get().getUserId()));
//            List<TransactionRequest> allTransactions = transactionService.findAllCustomerTransactions();
            List<TransactionRequest> filteredTransactions = transactionService.findAllCustomerTransactions()
                    .stream()
                    .filter(transaction -> {
                        if (transaction.getTransactionType().equals(TransactionType.CREDIT)) {
                            return transaction.getToUserId().equals(user.get().getUserId());
                        } else{
                            return transaction.getFromUserId().equals(user.get().getUserId());
                        }
                    })
                    .sorted(Comparator.comparing(TransactionRequest::getTransactionTime, Comparator.reverseOrder()))
                    .toList();
            //List<TransactionRequest> abc =  allTransactions.stream().filter(a->a.getFromUserId().equals(user.get().getUserId())).toList();
            return ResponseEntity.ok(filteredTransactions);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized or user not found");

    }
    private String getBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/approveTransaction")
    public ResponseEntity<String> approveAdminTransaction(@RequestBody ApprovalRequest approvalRequest) {
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
