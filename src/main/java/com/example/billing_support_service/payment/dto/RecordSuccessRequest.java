package com.example.billing_support_service.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordSuccessRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long amount;
}

