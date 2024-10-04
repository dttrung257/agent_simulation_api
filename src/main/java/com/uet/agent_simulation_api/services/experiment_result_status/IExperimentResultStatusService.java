package com.uet.agent_simulation_api.services.experiment_result_status;

import com.uet.agent_simulation_api.models.ExperimentResultStatus;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultStatusService {
    List<ExperimentResultStatus> get(BigInteger experimentId);
}
