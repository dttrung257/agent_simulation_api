package com.uet.agent_simulation_api.models.projections;

import java.math.BigInteger;

public interface SimulationRunProjection {
    BigInteger getId();
    BigInteger getExperimentResultId();
    String getExperimentName();
    String getModelName();
    Long getFinalStep();
}
