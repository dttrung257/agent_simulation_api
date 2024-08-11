package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.impl.ExperimentResultImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/experiment_result_images")
@RequiredArgsConstructor
public class ExperimentResultImageController {
    private final ResponseHandler responseHandler;
    private final ExperimentResultImageService experimentResultImageService;

    @GetMapping
    public ResponseEntity<SuccessResponse> get(
            @RequestParam(name = "experiment_result_id", required = false) BigInteger experimentResultId,
            @RequestParam(name = "experiment_id", required = false) BigInteger experimentId,
            @RequestParam(name = "model_id", required = false) BigInteger modelId,
            @RequestParam(name = "project_id", required = false) BigInteger projectId) {
        return responseHandler.respondSuccess(experimentResultImageService.get(experimentResultId, experimentId, modelId, projectId));
    }
}
