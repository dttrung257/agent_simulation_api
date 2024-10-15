package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentProgressResponse;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

public interface IExperimentResultService {
    List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId, Integer nodeId);

    ExperimentResult recreate(Experiment experiment, long finalStep, String outputDir);

    void updateStatus(ExperimentResult experimentResult, int status);

    ExperimentProgressResponse getExperimentProgress(BigInteger id);

    HashMap<String, BigInteger> getCurrentExperimentResultIds(CreateClusterSimulationRequest request);
}
