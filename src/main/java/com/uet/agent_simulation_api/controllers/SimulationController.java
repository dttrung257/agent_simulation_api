package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.services.SimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
public class SimulationController {
    private final SimulationService simulationService;

    @GetMapping
    public void getSimulation() {
        simulationService.startSimulation();
    }
}
