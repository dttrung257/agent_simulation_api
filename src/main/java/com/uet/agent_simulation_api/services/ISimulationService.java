package com.uet.agent_simulation_api.services;

import com.uet.agent_simulation_api.requests.CreateSimulationRequest;

public interface ISimulationService {
    /**
     * This method is used to run simulation.
     */
    void run();

    /**
     * This method is used to clear output of simulation.
     */
    void clear();

    void run(CreateSimulationRequest request);
}
