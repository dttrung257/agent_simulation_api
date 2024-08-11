package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.services.IAuthService;
import com.uet.agent_simulation_api.services.IExperimentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentResultService implements IExperimentResultService {
    private final IAuthService authService;
    private final ExperimentResultRepository experimentResultRepository;

    @Override
    public List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId) {
        return experimentResultRepository.find(authService.getCurrentUserId(), experimentId, modelId, projectId);
    }
}
