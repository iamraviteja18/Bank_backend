package com.bank.repository;

import com.bank.entities.TransactionRequest;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<TransactionRequest, String> {

   
}