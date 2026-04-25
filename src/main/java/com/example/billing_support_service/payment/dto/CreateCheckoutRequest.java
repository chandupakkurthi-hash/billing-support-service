package com.example.billing_support_service.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCheckoutRequest {
    @NotNull
    private Long price;
}

