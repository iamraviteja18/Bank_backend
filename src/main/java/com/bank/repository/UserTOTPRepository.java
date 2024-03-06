package com.bank.repository;

import com.bank.entities.UserTOTP;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserTOTPRepository extends MongoRepository<UserTOTP, String> {
    List<UserTOTP> findByUserName(String username);
}
