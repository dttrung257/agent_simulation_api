package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;
import com.uet.agent_simulation_api.pubsub.publisher.MessagePublisher;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.responses.simulation.RunSimulationResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.node.INodeService;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final IAuthService authService;
    private final INodeService nodeService;
    private final ResponseHandler responseHandler;
    private final MessagePublisher messagePublisher;
    private final ISimulationService simulationService;
    private final ExperimentResultRepository experimentResultRepository;

    private static final int RETRY_DELAY = 300;

    @PostMapping
    public ResponseEntity<SuccessResponse> runSimulation(@Valid @RequestBody CreateSimulationRequest request) {
        simulationService.run(request);

        final var resultIdList = new ArrayList<>();
        final var nodeId = nodeService.getCurrentNodeId();
        final var projectId = request.getProjectId();

        request.getExperiments().forEach((experiment) -> {
            final var experimentId = experiment.getId();
            final var modelId = experiment.getModelId();
            final var userId = authService.getCurrentUserId();
            final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId);
                experimentResultList.forEach((result) -> {
                    resultIdList.add(new RunSimulationResponse(nodeId, projectId, modelId, experimentId, result.getId()));
                });
        });

        return responseHandler.respondSuccess(resultIdList);
    }

    @PostMapping("/cluster")
    public ResponseEntity<SuccessResponse> runSimulationCluster(@Valid @RequestBody CreateClusterSimulationRequest request) {
        final var oldResultLastId = new HashMap<String, BigInteger>();
        request.getSimulationRequests().forEach((simulationRequest) -> {
            final var nodeId = simulationRequest.getNodeId();
            final var projectId = simulationRequest.getProjectId();

            simulationRequest.getExperiments().forEach((experiment) -> {
                final var experimentId = experiment.getId();
                final var modelId = experiment.getModelId();
                final var userId = authService.getCurrentUserId();
                final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId);

                if (experimentResultList.isEmpty()) {
                    final var key = nodeId + "-" + projectId + "-" + modelId + "-" + experimentId;
                    oldResultLastId.put(key, BigInteger.ZERO);
                } else {
                    experimentResultList.forEach((result) -> {
                        final var key = nodeId + "-" + projectId + "-" + modelId + "-" + experimentId;
                        final var lastId = oldResultLastId.getOrDefault(key, BigInteger.ZERO);

                        if (lastId.compareTo(result.getId()) < 0) {
                            oldResultLastId.put(key, result.getId());
                        }
                    });
                }
            });
        });

        request.getSimulationRequests().forEach((simulationRequest) -> {
            final var message = RunSimulation.builder()
                .nodeId(simulationRequest.getNodeId())
                .command(PubSubCommands.RUN_SIMULATION)
                .simulation(simulationRequest)
                .build();

            messagePublisher.publish(message);
        });

        final var resultIdList = new ArrayList<>();
        request.getSimulationRequests().forEach((simulationRequest) -> {
            final var nodeId = simulationRequest.getNodeId();
            final var projectId = simulationRequest.getProjectId();

            simulationRequest.getExperiments().forEach((experiment) -> {
                final var experimentId = experiment.getId();
                final var modelId = experiment.getModelId();
                final var userId = authService.getCurrentUserId();

                Optional<ExperimentResult> newResult = Optional.empty();
                int retryCount = 0;
                while (retryCount < 5) {
                    retryCount++;
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (Exception e) {
                        log.error("Error while waiting for new result", e);
                    }

                    final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId);

                    newResult = experimentResultList.stream()
                        .filter(result -> result.getId().compareTo(oldResultLastId.get(nodeId + "-" + projectId + "-" + modelId + "-" + experimentId)) > 0)
                        .max(Comparator.comparing(ExperimentResult::getId));

                    if (newResult.isPresent()) {
                        break;
                    }
                }


                newResult.ifPresent(result -> {
                    resultIdList.add(new RunSimulationResponse(nodeId, projectId, modelId, experimentId, result.getId()));
                });
            });
        });

        return responseHandler.respondSuccess(resultIdList);
    }

    @GetMapping("/publish")
    public ResponseEntity<SuccessResponse> publishSimulations() {
        return responseHandler.respondSuccess("Simulation is published");
    }
}
