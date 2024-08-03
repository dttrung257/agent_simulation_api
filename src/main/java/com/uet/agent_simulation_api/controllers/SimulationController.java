package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.redis.queue.MessagePublisher;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.ISimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final MessagePublisher messagePublisher;
    private final ResponseHandler responseHandler;
    private final ISimulationService simulationService;

    @PostMapping
    public ResponseEntity<SuccessResponse> runSimulations(@Valid @RequestBody CreateSimulationRequest request) {
        simulationService.run(request);

        return responseHandler.respondSuccess("Simulation is running");
    }

    @GetMapping("/publish")
    public ResponseEntity<SuccessResponse> publishSimulations() {
        messagePublisher.publish("Hello from Redis!");

        return responseHandler.respondSuccess("Simulation is published");
    }
}
