package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.services.SimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationServiceImpl implements SimulationService {
    private final ExecutorService virtualThreadExecutor;

    @Override
    public void startSimulation() {
        virtualThreadExecutor.submit(() -> {
            log.info("Starting simulation");

            ProcessBuilder processBuilder = new ProcessBuilder();
            try {
                for (int i = 0; i < 10; i++) {
                    (new ProcessBuilder()).command("bash", "-c", "touch file" + i + ".txt").start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            log.info("Simulation finished");
        });
    }
}
