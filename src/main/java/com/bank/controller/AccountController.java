package com.bank.controller;

import com.bank.entities.Account;
import com.bank.entities.Role;
import com.bank.entities.User;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import com.bank.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @GetMapping("/")
    public ResponseEntity<?> listUserAccounts(HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        logger.debug("username :::::::::::::::::::{}",username);
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isPresent()) {
            logger.debug("UserID : {}",user.get().getUserId());
            return ResponseEntity.ok(accountService.listAccountsByUserId(user.get().getUserId()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized or user not found");
    }

    private String getBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    @GetMapping("/all/")
    public ResponseEntity<?> listAllAccounts(HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }

        String username = jwtService.extractUserName(token);
        logger.debug("username :::::::::::::::::::{}",username);
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isPresent() && user.get().getRole().equals(Role.ADMIN)) {
            logger.debug("UserID : {}",user.get().getUserId());
            return ResponseEntity.ok(accountService.listAllAccounts());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized or user not found");
        }
    }

//    @GetMapping("/all/")
//    public ResponseEntity<?> listAllAccounts(@RequestParam("id") String userId) {
//        Optional<User> user = userRepository.findByUserId(userId);
//        if((user.isPresent()) && (user.get().getRole().equals(Role.ADMIN))) {
//            return ResponseEntity.ok(accountService.listAllAccounts());
//        }
//        else {
//            return ResponseEntity.ok("not found !!!!!");
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody Account account,HttpServletRequest request){
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }

        String username = jwtService.extractUserName(token);
        logger.debug("username :::::::::::::::::::{}",username);
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isPresent()) {
            account.setUserId(user.get().getUserId());
            logger.debug("username :::::::::::::::::::{}",account);
            accountService.createAccount(account);
            return ResponseEntity.ok("Account created successfully");
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized or user not found");
        }

    }

    @PutMapping
    public ResponseEntity<?> updateAccount(@RequestParam String accountId, @RequestBody Account accountDetails, HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        Optional<User> user = userRepository.findByEmail(username);

        if (user.isPresent()) {
            Optional<Account> accountOptional = accountService.getAccountByAccountNumber(accountId);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();

                logger.debug("before : {}",account);
                logger.debug("accountDetails : {}",account);
                accountService.updateAccount(account,accountDetails);
                return ResponseEntity.ok("Account updated successfully");
            } else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found or not authorized");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam String accountId, HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        Optional<User> user = userRepository.findByEmail(username);

        if (user.isPresent()) {
            Optional<Account> accountOptional = accountService.getAccountByAccountNumber(accountId);
            if (accountOptional.isPresent() && user.get().getUserId().equals(accountOptional.get().getUserId())) {
                Account account = accountOptional.get();

                logger.debug("before deleting: {}",account);
                accountService.deleteAccount(account.getAccountNumber());
                return ResponseEntity.ok("Account deleted successfully");
            } else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found or not authorized");
        }
    }



    // Additional endpoints for creating, updating, and deleting accounts
}
