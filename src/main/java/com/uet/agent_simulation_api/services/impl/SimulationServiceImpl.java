package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.services.SimulationService;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationServiceImpl implements SimulationService {
    private final TimeUtil timeUtil;
    private final ExecutorService virtualThreadExecutor;

    @Override
    public void run() {
//        virtualThreadExecutor.submit(() -> {
//        });
        long start = timeUtil.getCurrentTime();
        log.info("Simulation started at {}", timeUtil.getCurrentTimeString());

        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "/opt/gama-platform/headless/gama-headless.sh /Users/trungdt/Workspaces/uet/gama/pig-farm/models/sml-01.xml /Users/trungdt/Workspaces/uet/gama/pig-farm/models/test_output/";
        String commandBatchMode = "/opt/gama-platform/headless/gama-headless.sh -batch Normal /Users/trungdt/Workspaces/uet/gama/pig-farm/models/simulator-01.gaml";
        try {
            Process process = processBuilder.command("bash", "-c", command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Simulation ended at {}, took {} seconds",
                timeUtil.getCurrentTimeString(),
                (timeUtil.getCurrentTime() - start) / 1000
        );
    }
}
