package com.github.redayni.cinemaabyss.events.web.dto;

import tools.jackson.databind.JsonNode;

import java.time.Instant;

public record EventDto(String id, String type, Instant timestamp, JsonNode payload) {}