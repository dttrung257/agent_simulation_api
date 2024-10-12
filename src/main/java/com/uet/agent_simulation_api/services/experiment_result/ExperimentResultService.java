package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.constant.ExperimentResultStatusConst;
import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.node.INodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentResultService implements IExperimentResultService {
    private final IAuthService authService;
    private final INodeService nodeService;
    private final ExperimentResultRepository experimentResultRepository;

    @Override
    public List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId) {
        return experimentResultRepository.find(authService.getCurrentUserId(), experimentId, modelId, projectId);
    }

    @Override
    public ExperimentResult recreate(Experiment experiment, long finalStep) {
        experimentResultRepository.deleteByExperimentId(experiment.getId());

        // Save new experiment result.
        return experimentResultRepository.save(ExperimentResult.builder()
                .experiment(experiment)
                .location(null)
                .finalStep((int) finalStep)
                .node(nodeService.getCurrentNode())
                .status(ExperimentResultStatusConst.PENDING)
                .build());
    }

    @Override
    public void updateStatus(ExperimentResult experimentResult, int status) {
        experimentResult.setStatus(status);

        experimentResultRepository.save(experimentResult);
    }
}
