package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.commands.IGamaCommandBuilder;
import com.uet.agent_simulation_api.services.ISimulationService;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationService implements ISimulationService {
    private final TimeUtil timeUtil;
    private final ExecutorService virtualThreadExecutor;
    private final IGamaCommandBuilder gamaCommandBuilder;

    @Override
    public void run() {
        final long start = timeUtil.getCurrentTime();
        log.info("Simulation started at {}", timeUtil.getCurrentTimeString());

        final ProcessBuilder processBuilder = new ProcessBuilder();

        final String command = gamaCommandBuilder.buildLegacy(
                null,
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/sml-01.xml",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/test_output/"
        );
        final String batchCommand = gamaCommandBuilder.buildBatch(
                Map.of(
                        "-hpc", "2",
                        "-m", "8"
                ),
                "Normal",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/simulator-01.gaml"
        );

        try {
            log.info("Clear old output directory");
            processBuilder.command("bash", "-c", "rm -rf /Users/trungdt/Workspaces/uet/gama/pig-farm/includes/output/normal/**").start();

            log.info("Running command: {}", batchCommand);
            final Process process = processBuilder.command("bash", "-c", batchCommand).start();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Simulation ended at {}, took {} seconds",
                timeUtil.getCurrentTimeString(),
                (timeUtil.getCurrentTime() - start) * 1.0 / 1000
        );
    }

    @Override
    public void clear() {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            processBuilder.command("bash", "-c", "rm -rf /Users/trungdt/Workspaces/uet/gama/pig-farm/includes/output/normal").start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
