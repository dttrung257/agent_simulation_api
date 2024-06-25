package com.uet.agent_simulation_api.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSimulationRequest(
        @Valid @NotNull List<CreateExperimentRequest> experiments
) { }
