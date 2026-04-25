package com.example.billing_support_service.payment.service;

import com.example.billing_support_service.payment.entity.Transaction;
import com.example.billing_support_service.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final TransactionRepository transactionRepository;

    @Value("${subscription.expiry-minutes:1}")
    private long expiryMinutes;

    public Transaction recordSuccess(Long userId, Long amount) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setAmount(amount);
        tx.setPaymentStatus("SUCCESS");
        return transactionRepository.save(tx);
    }

    public List<Transaction> listTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByPaymentTimeDesc(userId);
    }

    public boolean isSubscriptionActive(Long userId) {
        Transaction latest = transactionRepository.findTopByUserIdOrderByPaymentTimeDesc(userId).orElse(null);
        if (latest == null) return false;
        if (!"SUCCESS".equalsIgnoreCase(latest.getPaymentStatus())) return false;
        LocalDateTime expiry = latest.getPaymentTime().plusMinutes(expiryMinutes);
        return expiry.isAfter(LocalDateTime.now());
    }
}

