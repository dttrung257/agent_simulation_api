package com.uet.agent_simulation_api.requests.simulation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Create experiment data request class.
 */
public record CreateExperimentDataRequest(
        @NotBlank(message = "GAML file must not be blank") String gamlFile,
        @NotBlank(message = "Experiment name must not be blank") String experimentName,
        @Min(value = 1, message = "Final step must be greater than or equal to 1") Long finalStep
) { }
