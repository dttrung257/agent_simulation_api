package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;
import com.uet.agent_simulation_api.pubsub.publisher.MessagePublisher;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.simulation.ISimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final MessagePublisher messagePublisher;
    private final ResponseHandler responseHandler;
    private final ISimulationService simulationService;

    @PostMapping
    public ResponseEntity<SuccessResponse> runSimulation(@Valid @RequestBody CreateSimulationRequest request) {
        simulationService.run(request);

        return responseHandler.respondSuccess("Simulation is running");
    }

    @PostMapping("/cluster")
    public ResponseEntity<SuccessResponse> runSimulationCluster(@Valid @RequestBody CreateClusterSimulationRequest request) {
        request.getSimulationRequests().forEach((simulationRequest) -> {
            final var message = RunSimulation.builder()
                .nodeId(simulationRequest.getNodeId())
                .command(PubSubCommands.RUN_SIMULATION)
                .simulation(simulationRequest)
                .build();

            messagePublisher.publish(message);
        });

        return responseHandler.respondSuccess("Simulation is running");
    }

    @GetMapping("/publish")
    public ResponseEntity<SuccessResponse> publishSimulations() {
        return responseHandler.respondSuccess("Simulation is published");
    }
}
