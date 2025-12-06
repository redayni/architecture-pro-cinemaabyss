package com.github.redayni.cinemaabyss.events.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/events/health")
    public Map<String, Boolean> health() {
        return Map.of("status", true);
    }
}
