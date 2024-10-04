package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.model.IModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {
    private final IModelService modelService;
    private final ResponseHandler responseHandler;

    @GetMapping
    public ResponseEntity<SuccessResponse> get(@RequestParam(name = "project_id", required = false) BigInteger projectId,
            @RequestParam(name = "has_experiment", required = false) Boolean hasExperiment) {
        return responseHandler.respondSuccess(modelService.get(projectId, hasExperiment));
    }
}
