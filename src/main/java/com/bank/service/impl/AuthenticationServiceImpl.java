package com.bank.service.impl;

import com.bank.dao.response.JwtAuthenticationResponse;
import com.bank.dao.response.SignupResponse;
import com.bank.entities.Account;
import com.bank.repository.UserRepository;
import com.bank.service.SequenceGeneratorService;
//import com.bank.service.StripeService;
import com.bank.service.TokenStore;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.dao.request.SignUpRequest;
import com.bank.dao.request.SigninRequest;
import com.bank.entities.Role;
import com.bank.entities.User;
import com.bank.service.AuthenticationService;
import com.bank.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        long sequence = sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME);
        user.setUserId(String.format("%08d", sequence));
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
//        return ResponseEntity.ok(SignupResponse.of(jwt, user));
        return JwtAuthenticationResponse.builder().token(jwt).userId(user.getUserId()).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);

        return JwtAuthenticationResponse.builder().token(jwt).userId(user.getUserId()).build();
    }

    @Override
    public void blacklistToken(String token) {
        jwtService.invalidateToken(token);
//        tokenStore.blacklistToken(token);
    }
}

