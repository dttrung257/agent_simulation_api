package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.ExperimentResult;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultService {
    List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId);

    ExperimentResult recreate(Experiment experiment, long finalStep);

    void updateStatus(ExperimentResult experimentResult, int status);
}
