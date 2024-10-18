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
import com.uet.agent_simulation_api.services.experiment_result.IExperimentResultService;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private final IExperimentResultService experimentResultService;
    private final ExperimentResultRepository experimentResultRepository;

    private static final int RETRY_DELAY = 300;
    private static final int MAX_RETRIES = 5;

    @PostMapping
    public ResponseEntity<SuccessResponse> runSimulation(@Valid @RequestBody CreateSimulationRequest request) {
        simulationService.run(request);

        final var resultIdList = new ArrayList<>();
        final var nodeId = nodeService.getCurrentNodeId();
        final var projectId = request.getProjectId();
        final var order = request.getOrder();

        request.getExperiments().forEach((experiment) -> {
            final var experimentId = experiment.getId();
            final var modelId = experiment.getModelId();
            final var userId = authService.getCurrentUserId();
            final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId);
                experimentResultList.forEach((result) -> {
                    resultIdList.add(new RunSimulationResponse(nodeId, projectId, modelId, experimentId, result.getId(), order));
                });
        });

        return responseHandler.respondSuccess(resultIdList);
    }

    @PostMapping("/cluster")
    public ResponseEntity<SuccessResponse> runSimulationCluster(@Valid @RequestBody CreateClusterSimulationRequest request) {
        // Get current result ids.
        final var oldResultIdMap = experimentResultService.getCurrentExperimentResultIds(request);

        final var currentNodeSimulationRequests = new ArrayList<CreateSimulationRequest>();
        // Run simulations in cluster
        request.getSimulationRequests().forEach((simulationRequest) -> {
            if (simulationRequest.getNodeId().equals(nodeService.getCurrentNodeId())) {
                currentNodeSimulationRequests.add(simulationRequest);
                return;
            }

            final var message = RunSimulation.builder()
                .nodeId(simulationRequest.getNodeId())
                .command(PubSubCommands.RUN_SIMULATION)
                .simulation(simulationRequest)
                .build();

            messagePublisher.publish(message);
        });
        currentNodeSimulationRequests.forEach(simulationService::run);

        // Wait for new results to be created
        final var resultIdList = new ArrayList<RunSimulationResponse>();
        List<CompletableFuture<Void>> futures = request.getSimulationRequests().stream().map(simulationRequest -> {
            final var nodeId = simulationRequest.getNodeId();
            final var projectId = simulationRequest.getProjectId();
            final var order = simulationRequest.getOrder();

            return CompletableFuture.runAsync(() -> {
                simulationRequest.getExperiments().forEach(experiment -> {
                    final var experimentId = experiment.getId();
                    final var modelId = experiment.getModelId();
                    final var userId = authService.getCurrentUserId();

                    retryUntilNewResultFound(userId, experimentId, modelId, projectId, nodeId, oldResultIdMap, MAX_RETRIES).thenAccept(newResult -> {
                        newResult.ifPresent(result -> {
                            resultIdList.add(new RunSimulationResponse(nodeId, projectId, modelId, experimentId, result.getId(), order));
                        });
                    }).join();
                });
            });
        }).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return responseHandler.respondSuccess(resultIdList);
    }

    private CompletableFuture<Optional<ExperimentResult>> retryUntilNewResultFound(BigInteger userId,
        BigInteger experimentId, BigInteger modelId, BigInteger projectId, Integer nodeId, Map<String, BigInteger> oldResultLastId, int maxRetries) {

        return CompletableFuture.supplyAsync(() -> {
            final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId);
            return experimentResultList.stream()
                .filter(result -> result.getId().compareTo(oldResultLastId.get(nodeId + "-" + projectId + "-" + modelId + "-" + experimentId)) > 0)
                .max(Comparator.comparing(ExperimentResult::getId));
        }).thenCompose(result -> {
            if (result.isPresent() || maxRetries <= 0) {
                return CompletableFuture.completedFuture(result);
            } else {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (Exception e) {
                        log.error("Error while waiting for new result: {}", e.getMessage());
                    }
                    return retryUntilNewResultFound(userId, experimentId, modelId, projectId, nodeId, oldResultLastId, maxRetries - 1).join();
                });
            }
        });
    }

    @GetMapping("/publish")
    public ResponseEntity<SuccessResponse> publishSimulations() {
        return responseHandler.respondSuccess("Simulation is published");
    }
}
