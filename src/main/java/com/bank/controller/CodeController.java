package com.bank.controller;

import com.bank.dao.ValidateCodeDto;
import com.bank.dao.Validation;
import com.bank.entities.UserTOTP;
import com.bank.repository.CredentialRepository;
import com.bank.repository.UserTOTPRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class CodeController {

    private final GoogleAuthenticator gAuth;
    private final CredentialRepository credentialRepository;
    private static final Logger logger = LoggerFactory.getLogger(CodeController.class);

    @Autowired
    private UserTOTPRepository userTOTPRepository;

    @SneakyThrows
    @GetMapping("/generate/")
    public void generate(@RequestBody UserTOTP username, HttpServletResponse response) {


        final GoogleAuthenticatorKey key = gAuth.createCredentials(username.getUserName());

//        List<UserTOTP> userTOTP = userTOTPRepository.findByUserName(username.getUserName());
//        long currentTimestampEpoch = Instant.now().getEpochSecond();
//        List<UserTOTP> new_userTOTP = userTOTP.stream().filter(item -> item.getTimestamp().equals("null")).collect(Collectors.toList());
//
//        if (new_userTOTP.notEmpty()) {
//            new_userTOTP.setTimestamp(String.valueOf(currentTimestampEpoch));
//        }

        List<UserTOTP> userTOTP = userTOTPRepository.findByUserName(username.getUserName());
        long currentTimestampEpoch = Instant.now().getEpochSecond();
        List<UserTOTP> new_userTOTP = userTOTP.stream()
                .filter(item -> item.getTimestamp() == null || item.getTimestamp().equals("null"))
                .toList();

        if (!new_userTOTP.isEmpty()) {
            new_userTOTP.forEach(item -> {
                item.setTimestamp(String.valueOf(currentTimestampEpoch));
                userTOTPRepository.save(item); // Assuming you have a save method to update each item
            });
        }

        logger.debug("Key :::: {}",key.getKey());
        //credentialRepository.saveUserCredentials(username.getUserName(), key.getKey(), key.getVerificationCode(), key.getScratchCodes());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("Bank", username.getUserName(), key);

        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.close();
    }

    @PostMapping("/validate/key")
    public Validation validateKey(@RequestBody ValidateCodeDto body) {
        return new Validation(gAuth.authorizeUser(body.getUsername(), body.getCode()));
    }

//    @GetMapping("/scratches/{username}")
//    public List<Integer> getScratches(@PathVariable String username) {
//        return getScratchCodes(username);
//    }
//
//    private List<Integer> getScratchCodes(@PathVariable String username) {
//        return credentialRepository.getUser(username).getScratchCodes();
//    }
//
//    @PostMapping("/scratches/")
//    public Validation validateScratch(@RequestBody ValidateCodeDto body) {
//        List<Integer> scratchCodes = getScratchCodes(body.getUsername());
//        Validation validation = new Validation(scratchCodes.contains(body.getCode()));
//        scratchCodes.remove(body.getCode());
//        return validation;
//    }
}