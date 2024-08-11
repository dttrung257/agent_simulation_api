package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.repositories.ExperimentResultImageRepository;
import com.uet.agent_simulation_api.services.IAuthService;
import com.uet.agent_simulation_api.services.IExperimentResultImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentResultImageService implements IExperimentResultImageService {
    private final IAuthService authService;
    private final ExperimentResultImageRepository experimentResultImageRepository;

    @Override
    public List<ExperimentResultImage> get(BigInteger experimentResultId, BigInteger experimentId, BigInteger modelId, BigInteger projectId) {
        return experimentResultImageRepository.find(authService.getCurrentUserId(), experimentResultId, experimentId, modelId, projectId);
    }
}
