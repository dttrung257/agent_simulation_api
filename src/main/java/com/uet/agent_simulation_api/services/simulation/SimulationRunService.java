package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.exceptions.errors.SimulationErrors;
import com.uet.agent_simulation_api.exceptions.simulation.SimulationRunNotFoundException;
import com.uet.agent_simulation_api.models.SimulationRun;
import com.uet.agent_simulation_api.models.projections.SimulationRunProjection;
import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.DeleteSimulationResult;
import com.uet.agent_simulation_api.pubsub.publisher.MessagePublisher;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.repositories.SimulationRunRepository;
import com.uet.agent_simulation_api.responses.simulation.SimulationDetailResponse;
import com.uet.agent_simulation_api.responses.simulation.SimulationHistoryResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationRunService implements ISimulationRunService {
    private final FileUtil fileUtil;
    private final IAuthService authService;
    private final INodeService nodeService;
    private final MessagePublisher messagePublisher;
    private final SimulationRunRepository simulationRunRepository;
    private final ExperimentResultRepository experimentResultRepository;

    @Override
    public SimulationRun create() {
        return simulationRunRepository.save(new SimulationRun());
    }

    @Override
    public List<SimulationHistoryResponse> getSimulationHistory(BigInteger projectId) {
        final var projections = simulationRunRepository.getSimulationHistory(
                projectId,
                authService.getCurrentUserId()
        );

        // Group by simulation run ID
        final Map<BigInteger, List<SimulationRunProjection>> groupedBySimulationId = projections.stream()
                .collect(Collectors.groupingBy(SimulationRunProjection::getId));

        // Convert to response format
        return groupedBySimulationId.entrySet().stream()
            .map(entry -> {
                final var resultIds = entry.getValue().stream()
                        .map(proj -> proj.getExperimentResultId().toString())
                        .collect(Collectors.joining(","));

                return new SimulationHistoryResponse(
                    entry.getKey(),
                    resultIds, // add resultIds vào constructor
                    entry.getValue().stream()
                        .map(proj -> new SimulationDetailResponse(
                                proj.getExperimentResultId(),
                                proj.getExperimentName(),
                                proj.getModelName(),
                                proj.getFinalStep()
                        ))
                        .toList()
                );
            })
            .toList();
    }

    @Override
    public void delete(BigInteger simulationId) {
        final var simulationRun = simulationRunRepository.findById(simulationId)
                .orElseThrow(() -> new SimulationRunNotFoundException(SimulationErrors.E_SM_0002.defaultMessage()));

        final var currentNodeId = nodeService.getCurrentNodeId();
        final var experimentResults = experimentResultRepository.findBySimulationRunId(simulationId);
        final List<BigInteger> currentNodeExperimentResultIdList = new ArrayList<>();
        experimentResults.forEach(er -> {
            final var nodeId = er.getNodeId();

            if (!nodeId.equals(currentNodeId)) {
                messagePublisher.publish(DeleteSimulationResult.builder()
                    .experimentResultId(er.getId())
                    .command(PubSubCommands.DELETE_SIMULATION_RESULT)
                    .nodeId(nodeId)
                    .build());
            }

            fileUtil.delete(er.getLocation());
            fileUtil.delete(er.getLocation() + ".zip");
            currentNodeExperimentResultIdList.add(er.getId());
        });

        experimentResultRepository.deleteByIds(currentNodeExperimentResultIdList);
        simulationRunRepository.delete(simulationRun);
    }
}
