package com.github.redayni.cinemaabyss.events.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record PaymentEventRequest(
    @JsonProperty("payment_id") @NotNull Integer paymentId,
    @JsonProperty("user_id") @NotNull Integer userId,
    @JsonProperty("amount") @NotNull Double amount,
    @JsonProperty("status") @NotBlank String status,
    @JsonProperty("timestamp") @NotNull Instant timestamp,
    @JsonProperty("method_type") String methodType
) {
}
