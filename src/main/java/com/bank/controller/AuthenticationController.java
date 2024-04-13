package com.bank.controller;

import com.bank.dao.request.SignUpRequest;
import com.bank.dao.request.SigninRequest;
import com.bank.dao.response.JwtAuthenticationResponse;
import com.bank.entities.User;
import com.bank.repository.CredentialRepository;
import com.bank.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.bank.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    GoogleAuthenticator gAuth;
    @Autowired
    CredentialRepository credentialRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        boolean emailExists = userRepository.existsByEmail(request.getEmail());
        if (emailExists) {
            // Return an error response if the email is already in use
            return ResponseEntity.badRequest().body("Error: User with email " + request.getEmail() + " already exists.");
        }else
        {
            return ResponseEntity.ok(authenticationService.signup(request));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        logger.debug("Request ::::::::::::::::: {}",request);
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authenticationService.blacklistToken(token);
            return ResponseEntity.ok().body("User logged out successfully.");
        }
        return ResponseEntity.badRequest().body("No active session found or invalid token.");
    }

}
