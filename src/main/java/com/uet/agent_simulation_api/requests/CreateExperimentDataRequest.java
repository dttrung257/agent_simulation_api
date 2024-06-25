package com.uet.agent_simulation_api.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateExperimentDataRequest(
        @NotBlank(message = "GAML file must not be blank") String gamlFile,
        @NotBlank(message = "Experiment name must not be blank") String experimentName,
        @Min(value = 1, message = "Final step must be greater than or equal to 1") Long finalStep
) { }
