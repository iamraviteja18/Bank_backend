package com.bank.controller;

import com.bank.entities.Payment;
import com.bank.entities.Refund;
import com.bank.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

//    @Autowired
//    private PaymentService paymentService;
//
//    @PostMapping("/processPayment")
//    public ResponseEntity<Payment> processPayment(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
//        try {
//            Payment payment = paymentService.processPayment(accountNumber, amount);
//            return ResponseEntity.ok(payment);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null); // Consider a more descriptive error handling
//        }
//    }
//
//    @PostMapping("/processRefund")
//    public ResponseEntity<Refund> processRefund(@RequestParam String paymentId) {
//        try {
//            Refund refund = paymentService.processRefund(paymentId);
//            return ResponseEntity.ok(refund);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null); // Consider a more descriptive error handling
//        }
//    }
}
