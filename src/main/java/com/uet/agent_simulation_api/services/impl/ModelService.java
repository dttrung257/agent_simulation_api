package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.models.Model;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.services.IAuthService;
import com.uet.agent_simulation_api.services.IModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService implements IModelService {
    private final IAuthService authService;
    private final ModelRepository modelRepository;

    @Override
    public List<Model> get(BigInteger projectId) {
        return modelRepository.find(authService.getCurrentUserId(), projectId);
    }
}
