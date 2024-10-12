package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.experiment_result_image.IExperimentResultImageService;
import com.uet.agent_simulation_api.services.image.IImageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/experiment_result_images")
@Validated
@RequiredArgsConstructor
public class ExperimentResultImageController {
    private final ResponseHandler responseHandler;
    private final IExperimentResultImageService experimentResultImageService;
    private final IImageService imageService;

    /**
     * Get experiment result images.
     *
     * @param experimentResultId BigInteger
     * @param experimentId BigInteger
     * @param modelId BigInteger
     * @param projectId BigInteger
     * @param experimentResultCategoryId BigInteger
     * @param step Integer
     *
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping
    public ResponseEntity<SuccessResponse> get(
            @RequestParam(name = "experiment_result_id", required = false) BigInteger experimentResultId,
            @RequestParam(name = "experiment_id", required = false) BigInteger experimentId,
            @RequestParam(name = "model_id", required = false) BigInteger modelId,
            @RequestParam(name = "project_id", required = false) BigInteger projectId,
            @RequestParam(name = "category_id", required = false) BigInteger experimentResultCategoryId,
            @RequestParam(name = "step", required = false) Integer step,
            @RequestParam(name = "page", required = false, defaultValue = "1")
            @Min(value = 1, message = "page must be greater than or equal to 1") Integer page,
            @RequestParam(name = "page_size", required = false, defaultValue = "10")
            @Max(value = 200, message = "page_size must be less than or equal to 200") Integer pageSize,
            @RequestParam(name = "order_by", required = false) String orderBy,
            @RequestParam(name = "order_direction", required = false) String orderDirection) {
        final var experimentResultImageRes = experimentResultImageService.get(experimentResultId, experimentId, modelId,
                projectId, experimentResultCategoryId, step, page, pageSize, orderBy, orderDirection);

        return responseHandler.respondSuccess(experimentResultImageRes.total(), experimentResultImageRes.data());
    }

    /**
     * Get an image by id.
     *
     * @param id BigInteger
     *
     * @return ResponseEntity<byte[]>
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable BigInteger id) {
        final var image = experimentResultImageService.getImageData(id);

        return ResponseEntity.ok().contentType(image.mediaType()).body(image.data());
    }

    /**
     * Get base64 encoded image data by id.
     *
     * @param id BigInteger
     *
     * @return ResponseEntity<String>
     */
    @GetMapping("/{id}/encode")
    public ResponseEntity<String> getImageEncode(@PathVariable BigInteger id) {
        return ResponseEntity.ok(experimentResultImageService.getImageDataEncoded(id));
    }
}
