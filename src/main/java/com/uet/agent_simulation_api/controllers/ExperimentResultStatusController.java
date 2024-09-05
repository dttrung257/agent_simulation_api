package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.impl.ExperimentResultStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/experiment_result_statuses")
@RequiredArgsConstructor
public class ExperimentResultStatusController {
    private final ResponseHandler responseHandler;
    private final ExperimentResultStatusService experimentResultStatusService;

    @GetMapping
    public ResponseEntity<SuccessResponse> get(@RequestParam(name = "experiment_id", required = false) BigInteger experimentId) {
        return responseHandler.respondSuccess(experimentResultStatusService.get(experimentId));
    }
}
