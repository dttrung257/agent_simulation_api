package com.uet.agent_simulation_api.services;

import com.uet.agent_simulation_api.models.ExperimentResultImage;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultImageService {
    List<ExperimentResultImage> get(BigInteger experimentResultId, BigInteger experimentId, BigInteger modelId, BigInteger projectId);
}
