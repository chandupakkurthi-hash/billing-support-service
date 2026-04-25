package com.example.billing_support_service.payment.repository;

import com.example.billing_support_service.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findTopByUserIdOrderByPaymentTimeDesc(Long userId);
    List<Transaction> findByUserIdOrderByPaymentTimeDesc(Long userId);
}

