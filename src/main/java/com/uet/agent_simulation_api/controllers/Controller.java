package com.uet.agent_simulation_api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/v1")
public class Controller {
    @Value("${server.port}")
    private int port;

    @Value("${spring.profiles.active}")
    private String profile;

    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Spring Boot application is running on port " + port + " with profile " + profile);
    }
}
