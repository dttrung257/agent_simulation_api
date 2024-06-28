package com.uet.agent_simulation_api.requests.simulation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Create experiment request class.
 */
public record CreateExperimentRequest(
        @Min(value = 0, message = "Experiment id must be greater than or equal to 1") @NotNull Integer id,
        @Valid CreateExperimentDataRequest data
) { }
