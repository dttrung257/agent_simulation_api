package com.uet.agent_simulation_api.services.experiment_result_image;

import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.responses.Pagination;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.responses.experiment_result_image.ExperimentResultImageDetailResponse;
import com.uet.agent_simulation_api.responses.experiment_result_image.ExperimentResultImageListResponse;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultImageService {
    Pagination<List<ExperimentResultImage>> get(BigInteger experimentResultId, BigInteger experimentId, BigInteger modelId,
        BigInteger projectId, BigInteger experimentResultCategoryId, Integer step, Integer page, Integer pageSize,
        String orderBy, String orderDirection);

    ExperimentResultImageDetailResponse getImageData(BigInteger id);

    String getImageDataEncoded(BigInteger id);

    ExperimentResultImageListResponse getByRange(BigInteger experimentResultId, Integer startStep, Integer endStep);
}
