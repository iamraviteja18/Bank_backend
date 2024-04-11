//package com.bank.controller;
//
//import com.bank.entities.Payment;
//import com.bank.entities.PaymentRequest;
//import com.bank.entities.Refund;
//import com.bank.entities.User;
//import com.bank.repository.UserRepository;
//import com.bank.service.JwtService;
//import com.bank.service.PaymentService;
//import com.bank.service.StripeService;
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.PaymentIntentCreateParams;
//import jakarta.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/payments")
//public class PaymentController {
//
//    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
//
//    @Autowired
//    private StripeService stripeService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    JwtService jwtService;
//
//    @PostMapping("/initiate")
//    @CrossOrigin(origins = "http://localhost:63342")
//    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
//        try {
//            logger.debug("paymentRequest :::::::::::::::::::{}",paymentRequest);
//            String clientSecret = stripeService.createPaymentIntent(
//                    paymentRequest.getAmount(),
//                    paymentRequest.getCustomerEmail(),
//                    paymentRequest.getPaymentMethodId(),
//                    paymentRequest.getClientIp(),
//                    paymentRequest.getClientUserAgent());
//
//            return ResponseEntity.ok().body(Map.of("clientSecret", clientSecret));
//        } catch (StripeException e) {
//            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    @PostMapping("/initiates")
//    public ResponseEntity<?> initiatePayments(@RequestBody PaymentRequest paymentRequest,  HttpServletRequest authToken) throws StripeException {
//        String token = getBearerToken(authToken);
//        if (token == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
//        }
//        try {// Extract userID from authToken
//            String username = jwtService.extractUserName(token);
//            logger.debug("username :::::::::::::::::::{}",username);
//            //Optional<User> user = userRepository.findByEmail(username);
//            // Fetch user from UserRepository to get Stripe bank account token
//            User user = userRepository.findByEmail(username)//userRepository.findByUserId(user.getUserId())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
//            logger.debug("user :::::::::::::::::::{}",user);
//            // Check if user has a Stripe bank account token
//            if (user.getStripeBankAccountToken() == null) {
//                throw new IllegalStateException("Stripe bank account token not found for user");
//            }
//        String paymentIntentId = stripeService.initiatePayments(
//                user.getStripeBankAccountToken(),
////                paymentRequest.getCustomerId(),
//                BigDecimal.valueOf(paymentRequest.getAmount())
//        );
//
//        // Include the client_secret in the response for frontend usage
//        Map<String, String> response = new HashMap<>();
//        response.put("paymentIntentId", paymentIntentId);
//        response.put("clientSecret", stripeService.getPaymentIntentClientSecret(paymentIntentId));
//        return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " + e.getMessage());
//        }
//    }
////    @PostMapping("/initiate")
//    @CrossOrigin(origins = "http://localhost:63342")
//    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequest paymentRequest,  HttpServletRequest authToken) {
//        String token = getBearerToken(authToken);
//        if (token == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
//        }
//        try {// Extract userID from authToken
//            String username = jwtService.extractUserName(token);
//            logger.debug("username :::::::::::::::::::{}",username);
//            //Optional<User> user = userRepository.findByEmail(username);
//            // Fetch user from UserRepository to get Stripe bank account token
//            User user = userRepository.findByEmail(username)//userRepository.findByUserId(user.getUserId())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
//            logger.debug("user :::::::::::::::::::{}",user);
//            // Check if user has a Stripe bank account token
//            if (user.getStripeBankAccountToken() == null) {
//                throw new IllegalStateException("Stripe bank account token not found for user");
//            }// Initiate payment with the Stripe bank account token
//            String paymentConfirmation = stripeService.initiatePayment(paymentRequest.getPaymentMethodId() ,user.getStripeBankAccountToken(), paymentRequest.getAmount());
//            return ResponseEntity.ok().body(paymentConfirmation);
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " + e.getMessage());
//        }
//    }
//    private String getBearerToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
////    @PostMapping("/initiatePayment")
////    public String initiatePayment(@RequestBody PaymentRequest paymentRequest) {
////        // Set your secret key. Remember to switch to your live secret key in production!
////        Stripe.apiKey = "sk_test_51P1up1CPlNIxsdNMjXYS7FoObsCR88YMcu8vqoBEoLBCt40GuCNq0GvvOzZMZMvfI4DZ5w0u8j1c8MB9BbQJI9IP005Z8npCGp";
////
////        Map<String, Object> params = new HashMap<>();
////        params.put("amount", paymentRequest.getAmount());
////        params.put("currency", "usd");
////        params.put("payment_method_types", java.util.List.of("us_bank_account"));
////        Map<String, Object> paymentMethodOptions = new HashMap<>();
////        Map<String, Object> usBankAccount = new HashMap<>();
////        usBankAccount.put("account_number", paymentRequest.getAccountNumber());
////        usBankAccount.put("routing_number", paymentRequest.getRoutingNumber());
////        usBankAccount.put("account_holder_type", "individual"); // or "company"
////        paymentMethodOptions.put("us_bank_account", usBankAccount);
////        params.put("payment_method_options", paymentMethodOptions);
////
////        try {
////            PaymentIntent intent = PaymentIntent.create(params);
////            return intent.toJson();
////        } catch (StripeException e) {
////            return "Payment initiation failed: " + e.getMessage();
////        }
////    }
////
////
////    private final String stripeApiKey = "sk_test_..."; // Use an environment variable in production
////
////    public PaymentController() {
////        Stripe.apiKey = stripeApiKey;
////    }
////
////    @PostMapping("/api/payments/initiate")
////    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
////        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
////                .setAmount(paymentRequest.getAmount())
////                .setCurrency(paymentRequest.getCurrency())
////                .setPaymentMethod(paymentRequest.getBankAccountToken())
////                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
////                .setConfirm(true)
////                .addPaymentMethodType("us_bank_account")
////                .build();
////
////        try {
////            PaymentIntent paymentIntent = PaymentIntent.create(params);
////            // Consider creating and saving a payment record in your DB here
////            return ResponseEntity.ok(paymentIntent);
////        } catch (StripeException e) {
////            e.printStackTrace();
////            return ResponseEntity.badRequest().body("Payment initiation failed: " + e.getMessage());
////        }
////    }
//
////    @Autowired
////    private PaymentService paymentService;
////
////    @PostMapping("/processPayment")
////    public ResponseEntity<Payment> processPayment(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
////        try {
////            Payment payment = paymentService.processPayment(accountNumber, amount);
////            return ResponseEntity.ok(payment);
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest().body(null); // Consider a more descriptive error handling
////        }
////    }
////
////    @PostMapping("/processRefund")
////    public ResponseEntity<Refund> processRefund(@RequestParam String paymentId) {
////        try {
////            Refund refund = paymentService.processRefund(paymentId);
////            return ResponseEntity.ok(refund);
////        } catch (IllegalArgumentException e) {
////            return ResponseEntity.badRequest().body(null); // Consider a more descriptive error handling
////        }
////    }
//}
