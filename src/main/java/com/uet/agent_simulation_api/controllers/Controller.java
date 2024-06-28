package com.uet.agent_simulation_api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uet.agent_simulation_api.models.AppUser;
import com.uet.agent_simulation_api.repositories.UserRepository;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.impl.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final ResponseHandler ResponseHandler;
    private final Environment env;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Value("${server.port}")
    private int port;

    @Value("${spring.profiles.active}")
    private String profile;

    @GetMapping
    public ResponseEntity<SuccessResponse> health() throws JsonProcessingException {
//        s3Service.uploadDirectory("./storage/outputs/1_simulator-01/snapshot", "snapshot");
//        s3Service.uploadFile("snapshot_download.png", "./storage/outputs/download.png");
//        s3Service.clearDirectory("snapshot");
//        final AppUser user = AppUser.builder()
//                .email("abcd@gmail.com")
//                .fullname("Nguyen Van A")
//                .password("123456")
//                .role(1)
//                .build();
//
//        userRepository.save(user);

        final AppUser user = userRepository.findById(BigInteger.valueOf(1)).orElse(null);
//        return ResponseHandler.respondSuccess(user);
        user.setCreatedBy("adminn");
        userRepository.save(user);

        return ResponseHandler.respondSuccess(user);

//        return ResponseHandler.respondSuccess(
//                "Server is running on port " + port + " with profiles: " + profile + ", thread: " + Thread.currentThread()
//        );
    }
}
