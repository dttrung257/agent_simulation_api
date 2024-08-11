package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ResponseHandler responseHandler;
    private final IProjectService projectService;

//    @PostMapping
//    public ResponseEntity<SuccessResponse> create(
//            @RequestPart("file_name") String fileName, @RequestPart("file") MultipartFile file) throws IOException {
//        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        Path uploadPath = Path.of("./storage");
//        Path filePath = uploadPath.resolve(uniqueFileName);
//
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        return responseHandler.respondSuccess(file.getOriginalFilename());
//    }

    @GetMapping
    public ResponseEntity<SuccessResponse> get() {
        return responseHandler.respondSuccess(projectService.get());
    }
}
