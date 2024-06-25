package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.requests.CreateSimulationRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.ISimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final ISimulationService simulationService;
    private final ResponseHandler responseHandler;

    @PostMapping
    public ResponseEntity<SuccessResponse> runSimulations(
            @Valid @RequestBody CreateSimulationRequest request
    ) {
        simulationService.run(request);

        return responseHandler.respondSuccess("Simulation is running");
    }

    @DeleteMapping
    public void clearSimulation() {
        simulationService.clear();
    }
}
