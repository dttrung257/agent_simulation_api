package com.uet.agent_simulation_api.services.experiment_result_image;

import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.responses.Pagination;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultImageService {
    Pagination<List<ExperimentResultImage>> get(BigInteger experimentResultId, BigInteger experimentId, BigInteger modelId,
        BigInteger projectId, BigInteger experimentResultCategoryId, Integer step, Integer page, Integer pageSize,
        String orderBy, String orderDirection);

    ExperimentResultImage getImage(BigInteger id);
}
