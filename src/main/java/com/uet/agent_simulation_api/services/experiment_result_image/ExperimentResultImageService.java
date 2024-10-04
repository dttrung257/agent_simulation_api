package com.uet.agent_simulation_api.services.experiment_result_image;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentResultImageErrors;
import com.uet.agent_simulation_api.exceptions.experiment_result_images.ExperimentResultImageNotFoundException;
import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.repositories.ExperimentResultImageRepository;
import com.uet.agent_simulation_api.responses.Pagination;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.utils.ConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentResultImageService implements IExperimentResultImageService {
    private final ConvertUtil convertUtil;
    private final IAuthService authService;
    private final ExperimentResultImageRepository experimentResultImageRepository;

    @Override
    public Pagination<List<ExperimentResultImage>> get(BigInteger experimentResultId, BigInteger experimentId, BigInteger modelId,
           BigInteger projectId, BigInteger experimentResultCategoryId, Integer step, Integer page, Integer pageSize,
           String orderBy, String orderDirection) {
        var sort = Sort.unsorted();
        final var sortFields = AppConst.EXPERIMENT_RESULT_IMAGE_SORT_FIELDS_NATIVE;
        sort = createSort(orderBy, orderDirection, sortFields);
        final var pageable = createPageRequest(page, pageSize, sort);

        final var experimentResultImagePage = experimentResultImageRepository.find(authService.getCurrentUserId(),
            experimentResultId, experimentId, modelId, projectId, experimentResultCategoryId, step, pageable);

        return new Pagination<>(BigInteger.valueOf(experimentResultImagePage.getTotalElements()),
                experimentResultImagePage.getContent());
    }

    private Sort createSort(String orderBy, String orderDirection, List<String> sortFields) {
        if (StringUtils.hasText(orderBy) && StringUtils.hasText(orderDirection) &&
                sortFields.contains(orderBy) && AppConst.ORDER_DIRECTIONS.contains(orderDirection)) {
            return Sort.by(Sort.Direction.valueOf(orderDirection.toUpperCase()), orderBy);
        }

        return Sort.unsorted();
    }

    private PageRequest createPageRequest(Integer page, Integer pageSize, Sort sort) {
        pageSize = pageSize != null && pageSize > AppConst.PAGE_MAX_SIZE ? AppConst.PAGE_MAX_SIZE : pageSize;

        return PageRequest.of(page - 1, pageSize, sort);
    }

    @Override
    public ExperimentResultImage getImage(BigInteger id) {
        return experimentResultImageRepository.findByIdAndUserId(id, authService.getCurrentUserId())
                .orElseThrow(() -> new ExperimentResultImageNotFoundException(ExperimentResultImageErrors.E_ERI_0001.defaultMessage()));
    }
}
