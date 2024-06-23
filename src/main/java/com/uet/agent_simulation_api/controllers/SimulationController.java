package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.services.ISimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
public class SimulationController {
    private final ISimulationService simulationService;

    @PostMapping
    public void getSimulation() {
        simulationService.run();
    }

    @DeleteMapping
    public void clearSimulation() {
        simulationService.clear();
    }
}
