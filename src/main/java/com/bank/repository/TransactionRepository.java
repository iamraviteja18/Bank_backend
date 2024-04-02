package com.bank.repository;

import com.bank.entities.PaymentStatus;
import com.bank.entities.Transaction;
import com.bank.entities.TransactionRequest;

import com.bank.entities.TransactionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<TransactionRequest, String> {
    List<TransactionRequest> findByStatus(PaymentStatus paymentStatus);

    Optional<TransactionRequest> findByTransactionId(String transactionId);
    // Example custom query method
//    List<Transaction> findByAccountIdAndType(String accountId, TransactionType type);
}