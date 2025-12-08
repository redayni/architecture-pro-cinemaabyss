package com.github.redayni.cinemaabyss.events.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.kafka.topics")
public record TopicsProperties(
    @NotBlank String movie,
    @NotBlank String user,
    @NotBlank String payment
) {}
