package com.github.redayni.cinemaabyss.events.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MovieEventRequest(
    @JsonProperty("movie_id") @NotNull Integer movieId,
    @JsonProperty("title") @NotBlank String title,
    @JsonProperty("action") @NotBlank String action,
    @JsonProperty("user_id") Integer userId,
    @JsonProperty("rating") Double rating,
    @JsonProperty("genres") List<String> genres,
    @JsonProperty("description") String description
) {}