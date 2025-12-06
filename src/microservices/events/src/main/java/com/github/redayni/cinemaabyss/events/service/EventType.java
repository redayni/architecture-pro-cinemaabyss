package com.github.redayni.cinemaabyss.events.service;

public enum EventType {
    MOVIE("movie"),
    USER("user"),
    PAYMENT("payment");

    private final String name;

    EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
