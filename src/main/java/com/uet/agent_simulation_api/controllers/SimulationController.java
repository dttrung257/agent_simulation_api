package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.constant.ExperimentResultStatusConst;
import com.uet.agent_simulation_api.constant.SimulationConst;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentErrors;
import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.errors.NodeErrors;
import com.uet.agent_simulation_api.exceptions.errors.ProjectErrors;
import com.uet.agent_simulation_api.exceptions.experiment.ExperimentNotFoundException;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
import com.uet.agent_simulation_api.exceptions.node.NodeNotFoundException;
import com.uet.agent_simulation_api.exceptions.project.ProjectNotFoundException;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;
import com.uet.agent_simulation_api.pubsub.publisher.MessagePublisher;
import com.uet.agent_simulation_api.repositories.ExperimentRepository;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.repositories.NodeRepository;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.requests.experiment_result.NewExperimentResult;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.responses.simulation.RunSimulationResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.experiment_result.IExperimentResultService;
import com.uet.agent_simulation_api.services.multi_simulation.IMultiSimulationService;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.services.simulation.ISimulationRunService;
import com.uet.agent_simulation_api.services.simulation.ISimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final IAuthService authService;
    private final INodeService nodeService;
    private final NodeRepository nodeRepository;
    private final ResponseHandler responseHandler;
    private final ModelRepository modelRepository;
    private final MessagePublisher messagePublisher;
    private final ProjectRepository projectRepository;
    private final ISimulationService simulationService;
    private final ExperimentRepository experimentRepository;
    private final ISimulationRunService simulationRunService;
    private final IExperimentResultService experimentResultService;
    private final ExperimentResultRepository experimentResultRepository;
    private final IMultiSimulationService multiSimulationService;

    @GetMapping
    public ResponseEntity<SuccessResponse> getSimulationHistory(
        @RequestParam(name = "project_id") BigInteger projectId
    ) {
        return responseHandler.respondSuccess(simulationRunService.getSimulationHistory(projectId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteSimulation(@PathVariable BigInteger id) {
        simulationRunService.delete(id);

        return responseHandler.respondSuccess("Simulation deleted successfully");
    }

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
            final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId, null);
                experimentResultList.forEach((result) -> {
                    resultIdList.add(new RunSimulationResponse(nodeId, projectId, modelId, experimentId, result.getId(), order));
                });
        });

        return responseHandler.respondSuccess(resultIdList);
    }

    @PostMapping("/cluster")
    public ResponseEntity<SuccessResponse> runSimulationCluster(@Valid @RequestBody CreateClusterSimulationRequest request) {
        request = experimentResultService.generateRequestNumber(request);

        // Prepare for run simulation in cluster
        final var newExperimentResultList = prepareForRunSimulationInCluster(request);

        // Create new experiment results
        final var resultIdList = createNewExperimentResult(newExperimentResultList, request);;

        final var currentNodeSimulationRequests = new ArrayList<CreateSimulationRequest>();
        // Run simulations in cluster
        request.getSimulationRequests().forEach((simulationRequest) -> {
            if (simulationRequest.getNodeId().equals(nodeService.getCurrentNodeId())) {
                currentNodeSimulationRequests.add(simulationRequest);
            } else {
                final var message = RunSimulation.builder()
                        .nodeId(simulationRequest.getNodeId())
                        .command(PubSubCommands.RUN_SIMULATION)
                        .simulation(simulationRequest)
                        .build();

                messagePublisher.publish(message);
            }
        });
        currentNodeSimulationRequests.forEach(simulationService::run);

        return responseHandler.respondSuccess(resultIdList);
    }

    private List<NewExperimentResult> prepareForRunSimulationInCluster(CreateClusterSimulationRequest request) {
        final ArrayList<NewExperimentResult> newExperimentResultList = new ArrayList<>();
        // Keep track of the last number used for each experiment ID
        final Map<BigInteger, BigInteger> experimentLastNumbers = new HashMap<>();
        AtomicBoolean clearOldMultiSimulationData = new AtomicBoolean(false);

        request.getSimulationRequests().forEach(simulationRequest -> {
            final var node = nodeRepository.findById(simulationRequest.getNodeId())
                    .orElseThrow(() -> new NodeNotFoundException(NodeErrors.E_NODE_0001.defaultMessage()));

            final var userId = authService.getCurrentUserId();
            final var projectId = simulationRequest.getProjectId();
            final var project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException(ProjectErrors.E_PJ_0001.defaultMessage()));
//            if (project.getName().contains("multi simulation")) {
//                clearOldMultiSimulationData.set(true);
//            }

            final var requestNumber = simulationRequest.getNumber();

            simulationRequest.getExperiments().forEach(experimentReq -> {
                // Get model
                final var model = modelRepository.findByModeIdAndProjectId(experimentReq.getModelId(), projectId)
                        .orElseThrow(() -> new ModelNotFoundException(ModelErrors.E_MODEL_0001.defaultMessage()));

                // Get experiment
                final var experiment = experimentRepository
                        .findByExperimentIdAndModelId(experimentReq.getId(), experimentReq.getModelId())
                        .orElseThrow(() -> new ExperimentNotFoundException(ExperimentErrors.E_EXP_0001.defaultMessage()));

                // Get or initialize the last number for this experiment
                BigInteger lastNumber = experimentLastNumbers.get(experiment.getId());
                if (lastNumber == null) {
                    BigInteger lastExperimentResultNumber = experimentResultService.getLastExperimentResultNumber(experiment.getId());
                    lastNumber = lastExperimentResultNumber == null ? BigInteger.ZERO : lastExperimentResultNumber;
                }

                // Increment the number and store it back in the map
                lastNumber = lastNumber.add(BigInteger.ONE);
                experimentLastNumbers.put(experiment.getId(), lastNumber);
                final var experimentResultNumber = lastNumber;

                experimentReq.setGamlFile(model.getName() + ".gaml");
                experimentReq.setExperiment(experiment);
                experimentReq.setExperimentResultNumber(experimentResultNumber);

                final var modelId = experimentReq.getModelId();
                final var experimentId = experimentReq.getId();
                final var gamlFile = experimentReq.getGamlFile();
                final var finalStep = experimentReq.getFinalStep() != null ? experimentReq.getFinalStep() : SimulationConst.DEFAULT_FINAL_STEP;
                final var gamlFileName = gamlFile.replace(".gaml", "");
                final var experimentName = experimentReq.getExperiment().getName();
                final var pathToLocalExperimentOutputDir = simulationService.getPathToLocalExperimentOutputDir(node.getId().toString(), userId, projectId,
                        modelId, experimentId, experimentResultNumber, gamlFileName, experimentName);

                newExperimentResultList.add(new NewExperimentResult(experimentReq.getExperiment(), finalStep,
                        pathToLocalExperimentOutputDir, experimentResultNumber, node, requestNumber));
            });
        });

//        if (clearOldMultiSimulationData.get()) {
//            multiSimulationService.clear();
//        }

        return newExperimentResultList;
    }

    private List<RunSimulationResponse> createNewExperimentResult(List<NewExperimentResult> newExperimentResultList, CreateClusterSimulationRequest request) {
        final var simulationRun = simulationRunService.create();

        final var resultIdList = new ArrayList<RunSimulationResponse>();
        final var experimentResultList = newExperimentResultList.stream()
            .map(newExperimentResult -> ExperimentResult.builder()
                .experiment(newExperimentResult.experiment())
                .location(newExperimentResult.pathToLocalExperimentOutputDir())
                .finalStep(newExperimentResult.finalStep().intValue())
                .node(newExperimentResult.node())
                .status(ExperimentResultStatusConst.PENDING)
                .number(newExperimentResult.experimentResultNumber())
                .simulationRunId(simulationRun.getId())
                .build())
            .toList();

        final var savedExperimentResultList = experimentResultRepository.saveAll(experimentResultList);

        request.getSimulationRequests().forEach(simulationRequest -> {
            simulationRequest.getExperiments().forEach(experimentRequest -> {
                final var experimentResultNumber = experimentRequest.getExperimentResultNumber();
                final ExperimentResult experimentResult = savedExperimentResultList.stream()
                        .filter(result -> result.getNumber().equals(experimentResultNumber)
                                && result.getExperiment().getId().equals(experimentRequest.getId()))
                        .findFirst().orElse(null);

                final var experimentResultId = experimentResult != null ? experimentResult.getId() : null;
                experimentRequest.setExperimentResult(experimentResult);

                resultIdList.add(new RunSimulationResponse(
                    simulationRequest.getNodeId(),
                    simulationRequest.getProjectId(),
                    experimentRequest.getModelId(),
                    experimentRequest.getId(),
                    experimentResultId,
                    simulationRequest.getOrder())
                );
            });
        });

        return resultIdList;
    }
}
