package com.bank.service.impl;

import com.bank.entities.Account;
import com.bank.entities.TransactionRequest;
import com.bank.entities.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<?> debit(String accountId, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountId);

        if(account!=null){
            if (account.getBalance().compareTo(amount) >= 0) {
                account.setBalance(account.getBalance().subtract(amount));
                accountRepository.save(account);
                TransactionRequest transactionRequest = new TransactionRequest();
                transactionRequest.setAccountId(accountId);
                transactionRequest.setAmount(amount);
                transactionRequest.setTransactionType(TransactionType.DEBIT);
                String transactionId = UUID.randomUUID().toString();
                transactionRequest.setTransactionId(transactionId);
                transactionRequest.setUserId(account.getUserId());
                transactionRepository.save(transactionRequest);
                return ResponseEntity.ok("Amount debited successfully");
            } else {
                return ResponseEntity.badRequest().body("Insufficient balance");
            }
        }
        return ResponseEntity.badRequest().body("Account not found");
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
