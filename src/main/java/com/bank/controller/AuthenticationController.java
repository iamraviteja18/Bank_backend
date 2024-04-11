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


//    @SneakyThrows
//    @GetMapping("/generate_new/")
//    public void generate(@RequestBody String username, HttpServletResponse response) {
//        final GoogleAuthenticatorKey key = gAuth.createCredentials(username);
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("my-demo", username, key);
//
//        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
//
//        ServletOutputStream outputStream = response.getOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
//        outputStream.close();
//    }

//    @GetMapping("/profile")
//    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
//        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
//    }
//
//    @PutMapping("/profile")
//    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody ProfileUpdateRequest request, Authentication authentication) {
//        return ResponseEntity.ok(userService.updateProfile(request, authentication.getName()));
//    }

}
