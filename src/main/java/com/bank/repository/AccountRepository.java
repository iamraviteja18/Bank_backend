package com.bank.repository;

import com.bank.entities.Account;
import com.bank.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {

    List<Account> findByUserId(String userId);

    Account findByAccountNumber(String accountNumber);

    void deleteByAccountNumber(String accountNumber);
}
