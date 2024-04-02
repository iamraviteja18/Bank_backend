package com.bank.repository;

import com.bank.entities.Refund;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefundRepository extends MongoRepository<Refund, String> {
    // Custom query methods if needed
}
