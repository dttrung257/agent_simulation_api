package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.responses.exeperiment_result.DownloadExperimentResultResponse;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentProgressResponse;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentResultDetailResponse;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

public interface IExperimentResultService {
    List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId, Integer nodeId);

    ExperimentResultDetailResponse getDetails(BigInteger id);

    ExperimentResult recreate(Experiment experiment, long finalStep, String outputDir, Integer experimentResultNumber);

    void updateStatus(ExperimentResult experimentResult, int status);

    ExperimentProgressResponse getExperimentProgress(BigInteger id);

    HashMap<String, BigInteger> getCurrentExperimentResultIds(CreateClusterSimulationRequest request);

    DownloadExperimentResultResponse download(BigInteger id);

    void stop(BigInteger id);

    CreateClusterSimulationRequest generateResultNumber(CreateClusterSimulationRequest request);
}
