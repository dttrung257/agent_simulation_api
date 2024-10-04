package com.uet.agent_simulation_api.services.experiment_result_status;

import com.uet.agent_simulation_api.models.ExperimentResultStatus;
import com.uet.agent_simulation_api.repositories.ExperimentResultStatusRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentResultStatusService implements IExperimentResultStatusService {
    private final IAuthService authService;
    private final ExperimentResultStatusRepository experimentResultStatusRepository;

    @Override
    public List<ExperimentResultStatus> get(BigInteger experimentId) {
        return experimentResultStatusRepository.findByUserIdAndExperimentId(authService.getCurrentUserId(), experimentId);
    }
}
