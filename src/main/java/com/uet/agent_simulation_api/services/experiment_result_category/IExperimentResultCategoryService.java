package com.uet.agent_simulation_api.services.experiment_result_category;

import com.uet.agent_simulation_api.models.ExperimentResultCategory;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultCategoryService {
    List<ExperimentResultCategory> get(BigInteger experimentResultId, BigInteger experimentId, BigInteger modelId,
           BigInteger projectId);
}
