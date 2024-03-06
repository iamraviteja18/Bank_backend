package com.bank.service;

import com.bank.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService {
    UserDetailsService userDetailsService();

    User findUserByUsername(String username);

    Optional<User> findByUserId(String userId);

    User updateUser(String id, User userDetails);
}
