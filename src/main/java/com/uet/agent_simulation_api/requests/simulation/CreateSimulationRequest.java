package com.uet.agent_simulation_api.requests.simulation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Create simulation request class.
 */
public record CreateSimulationRequest(
        @Valid @NotNull List<CreateExperimentRequest> experiments
) { }
