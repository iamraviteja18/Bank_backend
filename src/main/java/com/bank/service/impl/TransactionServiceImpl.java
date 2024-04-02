package com.bank.service.impl;

import com.bank.entities.Account;
import com.bank.entities.PaymentStatus;
import com.bank.entities.TransactionRequest;
import com.bank.entities.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;


    public List<TransactionRequest> findAllPendingTransactions() {
        return transactionRepository.findByStatus(PaymentStatus.PENDING);
    }

    public String approveTransaction(String transactionId, boolean approve) {
        // Step 1: Fetch the transaction
        Optional<TransactionRequest> transactions = transactionRepository.findByTransactionId(transactionId);
        if (transactions.isEmpty()) {
            throw new IllegalStateException("Transaction not found");
        } else {
            TransactionRequest transaction = transactions.get();
            if (!transaction.getStatus().equals(PaymentStatus.PENDING)) {
                throw new IllegalStateException("Transaction is not pending");
            }
            // Step 2: Approve or Decline the Transaction
            if (approve) {
                if (transaction.getTransactionType().equals(TransactionType.DEBIT)) {
                    // Correct use of orElseThrow with Optional
                    Account account = accountRepository.findByAccountNumber(transaction.getAccountId());
                    if (account == null) {
                        throw new IllegalStateException("Account not found");
                    }
                    if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                        transaction.setStatus(PaymentStatus.DECLINED);
                        transactionRepository.save(transaction);
                        throw new IllegalStateException("Insufficient funds");
                    }
                    // Deduct the amount from the account balance
                    account.setBalance(account.getBalance().subtract(transaction.getAmount()));
                    accountRepository.save(account); // Persist the updated account balance
                }
                transaction.setStatus(PaymentStatus.APPROVED);
            } else {
                transaction.setStatus(PaymentStatus.DECLINED);
            }
            // Step 3: Persist the Changes
            transactionRepository.save(transaction);
        }
        return approve ? "Transaction approved" : "Transaction declined";
    }


    public ResponseEntity<?> debit(String accountId, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountId);
        if(account != null) {
            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setAccountId(accountId);
            transactionRequest.setAmount(amount);
            transactionRequest.setTransactionType(TransactionType.DEBIT);
            transactionRequest.setTransactionId(UUID.randomUUID().toString());
            transactionRequest.setUserId(account.getUserId());
            transactionRequest.setTransactionTime(LocalDateTime.now()); // Set transaction time
            // Check for approval requirement
            if (amount.compareTo(new BigDecimal("1000")) > 0) {
                transactionRequest.setStatus(PaymentStatus.PENDING); // Set status to pending
                transactionRepository.save(transactionRequest);
                return ResponseEntity.ok("Transaction is pending approval.");
            } else { // Process transactions below $1000 immediately
                if (account.getBalance().compareTo(amount) >= 0) {
                    account.setBalance(account.getBalance().subtract(amount));
                    accountRepository.save(account);
                    transactionRequest.setStatus(PaymentStatus.APPROVED); // Set status to approved
                    transactionRepository.save(transactionRequest);
                    return ResponseEntity.ok("Amount debited successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Insufficient balance.");
                }
            }
        }
        return ResponseEntity.badRequest().body("Account not found.");
    }


    public ResponseEntity<?> credit(String accountId, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountId);
        //.orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (account != null) {
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);
            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setAccountId(accountId);
            transactionRequest.setAmount(amount);
            transactionRequest.setTransactionType(TransactionType.CREDIT);
            String transactionId = UUID.randomUUID().toString();
            transactionRequest.setTransactionId(transactionId);
            transactionRequest.setUserId(account.getUserId());
            transactionRepository.save(transactionRequest);
            return ResponseEntity.ok("Amount credited successfully");
        }
        return ResponseEntity.badRequest().body("Account not found");
    }

    @Override
    public ResponseEntity<?> transfer(String accountId, BigDecimal amount) {
        return null;
    }
}
