package com.example.billing_support_service.payment.controller;

import com.example.billing_support_service.payment.dto.CreateCheckoutRequest;
import com.example.billing_support_service.payment.dto.RecordSuccessRequest;
import com.example.billing_support_service.payment.entity.Transaction;
import com.example.billing_support_service.payment.service.BillingService;
import com.example.billing_support_service.payment.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${webui.base-url:http://localhost:8081}")
    private String webUiBaseUrl;

    private final StripeService stripeService;
    private final BillingService billingService;

    @PostMapping("/payment/create-checkout-session")
    public ResponseEntity<String> createCheckoutSession(@Valid @RequestBody CreateCheckoutRequest request) {
        String successUrl = webUiBaseUrl + "/success";
        String cancelUrl = webUiBaseUrl + "/cancel";
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return ResponseEntity.ok(successUrl);
        }

        try {
            Stripe.apiKey = stripeSecretKey;
            return ResponseEntity.ok(stripeService.createCheckoutSession(request.getPrice(), successUrl, cancelUrl));
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Stripe error: " + e.getMessage());
        }
    }

    @PostMapping("/billing/transactions/success")
    public ResponseEntity<Transaction> recordSuccess(@Valid @RequestBody RecordSuccessRequest request) {
        return ResponseEntity.ok(billingService.recordSuccess(request.getUserId(), request.getAmount()));
    }

    @GetMapping("/billing/transactions/{userId}")
    public ResponseEntity<List<Transaction>> listTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(billingService.listTransactions(userId));
    }

    @GetMapping("/billing/subscription/status")
    public ResponseEntity<Map<String, Object>> subscriptionStatus(@RequestParam("userId") Long userId) {
        boolean active = billingService.isSubscriptionActive(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "active", active));
    }
}
