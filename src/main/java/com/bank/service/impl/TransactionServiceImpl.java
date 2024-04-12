package com.bank.service.impl;

import com.bank.controller.TransactionController;
import com.bank.entities.*;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;


    public List<TransactionRequest> findAllPendingAdminTransactions() {
        return transactionRepository.findByStatus(PaymentStatus.PENDING_ADMIN);
    }

    public List<TransactionRequest> findAllPendingCustomerTransactions() {
        return transactionRepository.findAll();
    }

    public String approveTransaction(String transactionId, boolean approve) {
        // Step 1: Fetch the transaction
        Optional<TransactionRequest> transactions = transactionRepository.findByTransactionId(transactionId);
        if (transactions.isEmpty()) {
            throw new IllegalStateException("Transaction not found");
        } else {
            TransactionRequest transaction = transactions.get();
//            if (transaction.getStatus().equals(PaymentStatus.PENDING_ADMIN) | transaction.getStatus().equals(PaymentStatus.PENDING_CUSTOMER)) {
//                throw new IllegalStateException("Transaction is not pending");
//            }
//            TransactionRequest transaction = transactions.get();
//            if(transaction.getStatus().equals(PaymentStatus.PENDING_CUSTOMER))
//            {
//
//            }
//            else if (transaction.getStatus().equals(PaymentStatus.PENDING_ADMIN)) {
//                throw new IllegalStateException("Transaction is not pending");
//            }
//            else {
//                throw new IllegalStateException("Transaction is not pending");
//            }
            // Step 2: Approve or Decline the Transaction
            if (approve) {
                if (transaction.getTransactionType().equals(TransactionType.DEBIT)| transaction.getTransactionType().equals(TransactionType.REQUEST)) {
                    // Correct use of orElseThrow with Optional
                    Account account = accountRepository.findByAccountNumber(transaction.getFromAccountId());
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
            logger.debug("transaction:{}",transaction);
//            logger.debug(":{}",transaction);
            transactionRepository.save(transaction);
        }
        return approve ? "Transaction approved" : "Transaction declined";
    }


    public ResponseEntity<?> debit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        logger.debug("{}",account);
        if(account != null) {
            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setFromAccountId(accountNumber);
            transactionRequest.setToAccountId("00-00-00-00");
            transactionRequest.setAmount(amount);
            transactionRequest.setTransactionType(TransactionType.DEBIT);
            transactionRequest.setTransactionId(UUID.randomUUID().toString());
            transactionRequest.setFromUserId(account.getUserId());
            transactionRequest.setToUserId("11-11-11-11");
            transactionRequest.setTransactionTime(LocalDateTime.now()); // Set transaction time
            // Check for approval requirement
            if (amount.compareTo(new BigDecimal("1000")) > 0) {
                transactionRequest.setStatus(PaymentStatus.PENDING_ADMIN); // Set status to pending
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
            transactionRequest.setToAccountId(accountId);
            transactionRequest.setFromAccountId("00-00-00-00");
            transactionRequest.setAmount(amount);
            transactionRequest.setTransactionType(TransactionType.CREDIT);
            String transactionId = UUID.randomUUID().toString();
            transactionRequest.setTransactionId(transactionId);
            transactionRequest.setToUserId(account.getUserId());
            transactionRequest.setFromUserId("11-11-11-11");
            transactionRepository.save(transactionRequest);
            return ResponseEntity.ok("Amount credited successfully");
        }
        return ResponseEntity.badRequest().body("Account not found");
    }

    @Override
    public ResponseEntity<?> transfer(String accountId, BigDecimal amount) {
        return null;
    }

//    @Override
//    public ResponseEntity<?> transfer(String accountId, BigDecimal amount) {
//
//        Account account = accountRepository.findByAccountNumber(accountId);
//
//        if(account != null) {
//            TransactionRequest transactionRequest = new TransactionRequest();
////            transactionRequest.setAccountId(accountId);
//            transactionRequest.setAmount(amount);
//            transactionRequest.setTransactionType(TransactionType.DEBIT);
//            transactionRequest.setTransactionId(UUID.randomUUID().toString());
////            transactionRequest.setUserId(account.getUserId());
//            transactionRequest.setTransactionTime(LocalDateTime.now()); // Set transaction time
//            // Check for approval requirement
//            if (amount.compareTo(new BigDecimal("1000")) > 0) {
//                transactionRequest.setStatus(PaymentStatus.PENDING); // Set status to pending
//                transactionRepository.save(transactionRequest);
//                return ResponseEntity.ok("Transaction is pending approval.");
//            } else { // Process transactions below $1000 immediately
//                if (account.getBalance().compareTo(amount) >= 0) {
//                    account.setBalance(account.getBalance().subtract(amount));
//                    accountRepository.save(account);
//                    transactionRequest.setStatus(PaymentStatus.APPROVED); // Set status to approved
//                    transactionRepository.save(transactionRequest);
//                    return ResponseEntity.ok("Amount debited successfully.");
//                } else {
//                    return ResponseEntity.badRequest().body("Insufficient balance.");
//                }
//            }
//        }
//        return ResponseEntity.badRequest().body("Account not found.");
////        return null;
//    }

    public ResponseEntity<?> requestFunds(String fromAccountId, String toAccountId, BigDecimal amount) {
        // Retrieve both accounts
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountId);
        Account toAccount = accountRepository.findByAccountNumber(toAccountId);
        // Check if both accounts exist
        if (fromAccount == null) {
            return ResponseEntity.badRequest().body("From account not found.");
        }
        if (toAccount == null) {
            return ResponseEntity.badRequest().body("To account not found.");
        }
        TransactionRequest debitTransaction = new TransactionRequest();
        debitTransaction.setFromAccountId(fromAccountId);
        debitTransaction.setToAccountId(toAccountId);
        debitTransaction.setFromUserId(fromAccount.getUserId());
        debitTransaction.setToUserId(toAccount.getUserId());
        debitTransaction.setAmount(amount);
        debitTransaction.setTransactionType(TransactionType.TRANSFER);
        debitTransaction.setTransactionId(UUID.randomUUID().toString());
        debitTransaction.setTransactionTime(LocalDateTime.now());
        debitTransaction.setStatus(PaymentStatus.PENDING_CUSTOMER); // Set status to approved
        transactionRepository.save(debitTransaction);
        return ResponseEntity.ok("Request is in PENDING_CUSTOMER state");
    }

    public ResponseEntity<?> transferFunds(String fromAccountId, String toAccountId, BigDecimal amount) {
        return ResponseEntity.ok("Transfer completed successfully.");
    }
}
