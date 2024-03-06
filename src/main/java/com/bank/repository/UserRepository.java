package com.bank.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entities.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Since email is unique, we'll find users by email
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    User findByUsername(String username);

    Optional<User> findByUserId(String userId);
}
