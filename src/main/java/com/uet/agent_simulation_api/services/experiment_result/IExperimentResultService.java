package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.models.ExperimentResult;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultService {
    List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId);
}
