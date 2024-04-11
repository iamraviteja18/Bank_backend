//package com.bank.service;
//
//import com.bank.entities.Payment;
//import com.bank.repository.PaymentRepository;
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Customer;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.CustomerCreateParams;
//import com.stripe.param.CustomerListParams;
//import com.stripe.param.PaymentIntentCreateParams;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class StripeService {
//
//    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);
//    private final PaymentRepository paymentRepository;
//
//    @Autowired
//    public StripeService(PaymentRepository paymentRepository, @Value("${stripe.api.key}") String apiKey) {
//        this.paymentRepository = paymentRepository;
//        Stripe.apiKey = apiKey;
//    }
//
//    public String createPaymentIntents(Long amount, String customerEmail, String paymentMethodId) throws StripeException {
//        // Create or retrieve Stripe Customer
//        CustomerCreateParams customerParams = CustomerCreateParams.builder()
//                .setEmail(customerEmail)
//                .build();
//        Customer customer = Customer.create(customerParams);
//
//        // Create PaymentIntent
//        PaymentIntentCreateParams paymentIntentParams = PaymentIntentCreateParams.builder()
//                .setAmount(amount * 100) // Convert to cents
//                .setCurrency("usd")
//                .setCustomer(customer.getId())
//                .addPaymentMethodType("us_bank_account")
//                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
//                .build();
//
//        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentParams);
//        return paymentIntent.getClientSecret();
//    }
//
//    public Map<String, String> confirmPayment(String paymentIntentId) throws StripeException {
//        // Retrieve the PaymentIntent to get the latest status
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
//
//        // Check PaymentIntent status (should be confirmed after user interaction)
//        if (paymentIntent.getStatus().equals("succeeded")) {
//            // Payment successful, process on your backend (e.g., capture funds)
//            // ... your logic to process successful payment ...
//            return Collections.singletonMap("message", "Payment successful!");
//        } else {
//            throw new StripeException("Payment confirmation failed. Status: " + paymentIntent.getStatus());
//        }
//    }
//    public String createPaymentIntent(Long amount, String customerEmail, String paymentMethodId,String clientIp,String clientUserAgent) throws StripeException {
////        Stripe.apiKey = "sk_test_..."; // Use your actual secret key
//
//        // Try to retrieve an existing customer by email
//        CustomerListParams listParams = CustomerListParams.builder()
//                .setEmail(customerEmail)
//                .build();
//        List<Customer> customers = Customer.list(listParams).getData();
//
//        // Use the existing customer or create a new one if not found
//        Customer customer;
//        if (!customers.isEmpty()) {
//            logger.debug("customer exists :::: {}",customers.get(0));
//            customer = customers.get(0);
//        } else {
//            CustomerCreateParams customerParams = CustomerCreateParams.builder()
//                    .setEmail(customerEmail)
//                    .build();
//            customer = Customer.create(customerParams);
//        }
//        // Prepare mandate_data for the PaymentIntent
//        PaymentIntentCreateParams.MandateData mandateData = PaymentIntentCreateParams.MandateData.builder()
//                .setCustomerAcceptance(PaymentIntentCreateParams.MandateData.CustomerAcceptance.builder()
//                        .setType(PaymentIntentCreateParams.MandateData.CustomerAcceptance.Type.ONLINE)
//                        .setOnline(PaymentIntentCreateParams.MandateData.CustomerAcceptance.Online.builder()
//                                .setIpAddress(clientIp) // Obtain and set the customer's IP address dynamically
//                                .setUserAgent(clientUserAgent) // Obtain and set the customer's User Agent dynamically
//                                .build())
//                        .build())
//                .build();
//
//        // Create PaymentIntent with the payment method attached and mandate data
//        PaymentIntentCreateParams paymentIntentParams = PaymentIntentCreateParams.builder()
//                .setAmount(amount * 100) // Convert to cents
//                .setCurrency("usd")
//                .setCustomer(customer.getId())
//                .setPaymentMethod(paymentMethodId)
//                .addPaymentMethodType("us_bank_account")
//                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
//                .setConfirm(true) // Automatically confirm the PaymentIntent
//                .setMandateData(mandateData) // Include mandate data
//                //.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
//                .setOffSession(true)
//                .setConfirm(true)
//                .build();
//
//
//        // Create PaymentIntent with the payment method attached
////        PaymentIntentCreateParams paymentIntentParams = PaymentIntentCreateParams.builder()
////                .setAmount(amount * 100) // Convert to cents
////                .setCurrency("usd")
////                .setCustomer(customer.getId())
////                .setPaymentMethod(paymentMethodId)
////                .addPaymentMethodType("us_bank_account")
////                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
////                .setConfirm(true) // Automatically confirm the PaymentIntent
////                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
////                .build();
//
//        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentParams);
//        logger.debug("paymentIntent.getStatus() :::: {}",paymentIntent);
////        paymentIntent.getStatus();
//        return paymentIntent.getClientSecret();
//    }
//
//    public String initiatePayments(String customerId, BigDecimal amount) throws StripeException {
//        // Create a PaymentIntent with the specified customer ID and amount
//        Map<String, Object> params = new HashMap<>();
//        params.put("customer", customerId);
//        params.put("amount", amount);
//        params.put("currency", "usd"); // Adjust if needed
//        params.put("payment_method_types", Collections.singletonList("us_bank_account"));
//        params.put("confirmation_method", "automatic");
//        // Consider adding other relevant fields based on your requirements (e.g., payment_method_options)
//
//        PaymentIntent paymentIntent = PaymentIntent.create(params);
//        Payment payment = new Payment();
//        payment.setStripePaymentIntentId(paymentIntent.getId());
//        payment.setAmount(amount); // Consider storing as cents
//        payment.setStatus(paymentIntent.getStatus());
//        payment.setTimestamp(Instant.now());
//        payment.setCurrency("usd"); // Consider making dynamic
//        paymentRepository.save(payment);
//        return paymentIntent.getId();
//    }
//    public String initiatePayment(String paymentMethodId, String customerId, Long amount) throws StripeException {
//        long amountInCents = amount * 100; // Convert dollars to cents
////        logger.debug("bankAccountToken :::: {}",bankAccountToken);
//        try {
//            PaymentIntentCreateParams params =
//                    PaymentIntentCreateParams.builder()
//                            .setAmount(amountInCents)
//                            .setCurrency("usd")
//                            .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
//                            .setCustomer(customerId)
//                            .addPaymentMethodType("us_bank_account")
//                            .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//            logger.debug("params :::: {}",params.toString());
//            PaymentIntent intent = PaymentIntent.create(params);
//            logger.debug("intent :::: {}",intent.toString());
//            // Save payment details in MongoDB
//            Payment payment = new Payment();
//            payment.setStripePaymentIntentId(intent.getId());
//            payment.setAmount(BigDecimal.valueOf(amount)); // Consider storing as cents
//            payment.setStatus(intent.getStatus());
//            payment.setTimestamp(Instant.now());
//            payment.setCurrency("usd"); // Consider making dynamic
//            paymentRepository.save(payment);
//            return intent.getStatus();
//        } catch (StripeException e) {
//            // Log and handle Stripe API exceptions
//            throw e; // Or handle more gracefully
//        } catch (Exception e) {
//            // Handle other exceptions, e.g., database errors
//            throw new RuntimeException("Failed to initiate payment: " + e.getMessage(), e);
//        }
//    }
//
////    public String initiatePayment(String bankAccountToken, Long amount) throws StripeException {
////        long amountInCents = amount * 100;// Ensure this is in the smallest currency unit (e.g., cents for USD)
////
////        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
////                .setAmount(amountInCents)
////                .setCurrency("usd")
//////                .setPaymentMethodTypes(java.util.List.of("us_bank_account"))
////                .setPaymentMethod(bankAccountToken)
////                .setConfirm(true)
////                .build();
////
////        PaymentIntent intent = PaymentIntent.create(params);
////
////        // Save payment details in MongoDB
////        Payment payment = new Payment();
////        payment.setStripePaymentIntentId(intent.getId());
////        payment.setAmount(BigDecimal.valueOf(amount));
////        payment.setStatus(intent.getStatus());
////        payment.setTimestamp(Instant.now());
////        payment.setCurrency("usd"); // Set this dynamically if you support multiple currencies
////        paymentRepository.save(payment);
////
////        return intent.getStatus();
////    }
//
//    public String createStripeCustomer(String email) throws StripeException {
//        // Ensure the API key is set for this request
//
//        // Prepare customer creation parameters
//        CustomerCreateParams params = CustomerCreateParams.builder()
//                .setEmail(email)
//                .build();
//
//        // Create the customer in Stripe
//        Customer customer = Customer.create(params);
//
//        // Return the customer ID
//        return customer.getId();
//    }
//
//    public String getPaymentIntentClientSecret(String paymentIntentId) throws StripeException {
//        // Retrieve the PaymentIntent object by its ID
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
//        return paymentIntent.getClientSecret();
//    }
//}
