package com.uet.agent_simulation_api.services;

import com.uet.agent_simulation_api.models.Experiment;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentService {
    List<Experiment> get(BigInteger projectId, BigInteger modelId);
}
