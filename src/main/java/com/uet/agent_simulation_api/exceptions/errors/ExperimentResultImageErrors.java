package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store experiment result image errors.
 */
public final class ExperimentResultImageErrors {
    public static final ErrorDetails E_ERI_0001 = new ErrorDetails(HttpStatus.NOT_FOUND.value(),
            "E_ERI_0001", "Experiment result image not found");
}
