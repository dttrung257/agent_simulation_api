package com.uet.agent_simulation_api.services.experiment_result_image;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentResultImageErrors;
import com.uet.agent_simulation_api.exceptions.experiment_result_images.ExperimentResultImageDataReadException;
import com.uet.agent_simulation_api.exceptions.experiment_result_images.ExperimentResultImageNotFoundException;
import com.uet.agent_simulation_api.exceptions.node.CannotFetchNodeDataException;
import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.models.projections.ExperimentResultImageDetailProjection;
import com.uet.agent_simulation_api.repositories.ExperimentResultImageRepository;
import com.uet.agent_simulation_api.responses.Pagination;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.responses.experiment_result_image.ExperimentResultImageDetailResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.image.IImageService;
import com.uet.agent_simulation_api.services.node.INodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentResultImageService implements IExperimentResultImageService {
    private final IAuthService authService;
    private final INodeService nodeService;
    private final IImageService imageService;
    private final ExperimentResultImageRepository experimentResultImageRepository;

    @Override
    public Pagination<List<ExperimentResultImage>> get(BigInteger experimentResultId, BigInteger experimentId,
            BigInteger modelId, BigInteger projectId, BigInteger experimentResultCategoryId, Integer step, Integer page,
            Integer pageSize, String orderBy, String orderDirection) {
        final var sortFields = AppConst.EXPERIMENT_RESULT_IMAGE_SORT_FIELDS_NATIVE;
        final var sort = createSort(orderBy, orderDirection, sortFields);
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
    public ExperimentResultImageDetailResponse getImageData(BigInteger id) {
        final var experimentResultImageDetailProjection = experimentResultImageRepository.findDetailById(id, authService.getCurrentUserId())
                .orElseThrow(() -> new ExperimentResultImageNotFoundException(ExperimentResultImageErrors.E_ERI_0001.defaultMessage()));

        checkImageData(experimentResultImageDetailProjection);

        if (isCurrentNodeImage(nodeService.getCurrentNodeId(), experimentResultImageDetailProjection.getNodeId())) {
            return getCurrentNodeImage(experimentResultImageDetailProjection);
        }

        return getImageInNode(experimentResultImageDetailProjection.getId(), experimentResultImageDetailProjection.getNodeId());
    }

    private void checkImageData(ExperimentResultImageDetailProjection experimentResultImageDetailProjection) {
        final var isValid = experimentResultImageDetailProjection != null
                && experimentResultImageDetailProjection.getNodeId() != null
                && experimentResultImageDetailProjection.getLocation() != null
                && experimentResultImageDetailProjection.getExtension() != null;

        if (!isValid) {
            throw new ExperimentResultImageNotFoundException(ExperimentResultImageErrors.E_ERI_0001.defaultMessage());
        }
    }

    private Boolean isCurrentNodeImage(Integer currentNodeId, Integer imageNodeId) {
        return currentNodeId.equals(imageNodeId);
    }

    private ExperimentResultImageDetailResponse getCurrentNodeImage(
            ExperimentResultImageDetailProjection experimentResultImageDetailProjection) {
        final var filePath = Paths.get(experimentResultImageDetailProjection.getLocation());
        if (!Files.exists((filePath))) {
            throw new ExperimentResultImageNotFoundException("This image does no longer exist in the server.");
        }

        final var imageData = readImage(filePath);
        final var mediaType = imageService.getImageMediaType(experimentResultImageDetailProjection.getExtension());

        return new ExperimentResultImageDetailResponse(mediaType, imageData);
    }

    private byte[] readImage(Path filePath) {
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new ExperimentResultImageDataReadException(ExperimentResultImageErrors.E_ERI_0002.defaultMessage());
        }
    }

    private ExperimentResultImageDetailResponse getImageInNode(BigInteger imageId, Integer nodeId) {
        final var webClient = nodeService.getWebClientByNodeId(nodeId);

        return fetchImage(webClient, imageId);
    }

    private ExperimentResultImageDetailResponse fetchImage(WebClient webClient, BigInteger imageId) {
        try {
            final var response = webClient.get().uri("api/v1/experiment_result_images/" + imageId)
                    .retrieve().bodyToMono(SuccessResponse.class).block();

            if (response == null) {
                throw new ExperimentResultImageNotFoundException(ExperimentResultImageErrors.E_ERI_0001.defaultMessage());
            }

            return (ExperimentResultImageDetailResponse) response.data();
        } catch (Exception e) {
            log.error("Error while fetching image: {}", e.getMessage());

            throw new CannotFetchNodeDataException(e.getMessage());
        }
    }
}
