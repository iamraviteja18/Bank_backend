package com.bank.repository;

import com.bank.entities.Payment;
import com.bank.entities.Refund;
import com.bank.entities.Transaction;
import com.bank.entities.TransactionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    // Custom query methods if needed
}
