package com.uet.agent_simulation_api.services;

import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;

/**
 * Simulation service interface.
 */
public interface ISimulationService {
    /**
     * This method is used to run simulation.
     */
    void run();

    /**
     * This method is used to run simulation with a specific request.
     *
     * @param request CreateSimulationRequest
     */
    void run(CreateSimulationRequest request);
}
