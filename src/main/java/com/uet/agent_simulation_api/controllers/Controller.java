package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final ResponseHandler ResponseHandler;

    @Value("${server.port}")
    private int port;

    @Value("${spring.profiles.active}")
    private String profile;

    @GetMapping("/health")
    public ResponseEntity<SuccessResponse> health() {
        return ResponseHandler.respondSuccess("Ok");
    }
}
