package com.github.redayni.cinemaabyss.events.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserEventRequest(
    @JsonProperty("user_id") @NotNull Integer userId,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("action") @NotBlank String action,
    @JsonProperty("timestamp") @NotNull Instant timestamp
) {
}