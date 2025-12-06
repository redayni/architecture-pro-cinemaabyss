package com.github.redayni.cinemaabyss.events.web.dto;

public record EventResponse(String status, int partition, long offset, EventDto event) {}