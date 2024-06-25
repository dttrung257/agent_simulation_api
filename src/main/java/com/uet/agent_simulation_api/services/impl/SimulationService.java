package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.commands.builders.IGamaCommandBuilder;
import com.uet.agent_simulation_api.commands.executors.IGamaCommandExecutor;
import com.uet.agent_simulation_api.constant.SimulationConst;
import com.uet.agent_simulation_api.requests.CreateSimulationRequest;
import com.uet.agent_simulation_api.services.ISimulationService;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationService implements ISimulationService {
    private final TimeUtil timeUtil;
    private final IGamaCommandBuilder gamaCommandBuilder;
    private final IGamaCommandExecutor gamaCommandExecutor;

    @Override
    public void run() {
        final long start = timeUtil.getCurrentTime();
        log.info("Simulation started at {}", timeUtil.getCurrentTimeString());

        final ProcessBuilder processBuilder = new ProcessBuilder();

        final String command = gamaCommandBuilder.buildLegacy(
                Map.of(
                        "-hpc", "8",
                        "-m", "8"
                ),
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/sml-01.xml",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/test_output/"
        );
        /*
        final String batchCommand = gamaCommandBuilder.buildBatch(
                Map.of(
                        "-hpc", "2",
                        "-m", "8"
                ),
                "Normal",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/simulator-01.gaml"
        );
        */

        try {
            log.info("Clear old output directory");
            processBuilder.command("bash", "-c", "rm -rf /Users/trungdt/Workspaces/uet/gama/pig-farm/includes/output/normal/**").start();

            log.info("Running command: {}", command);
            final Process process = processBuilder.command("bash", "-c", command).start();

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
    public void run(CreateSimulationRequest request) {
        final var experiments = request.experiments();
        experiments.forEach(experiment -> {
            // Get experiment data from request
            final int experimentId = experiment.id();
            final var experimentData = experiment.data();
            final String gamlFile = experimentData.gamlFile();
            final String experimentName = experimentData.experimentName();
            final long finalStep = experimentData.finalStep() != null ? experimentData.finalStep() : SimulationConst.DEFAULT_FINAL_STEP;

            final String fileName = gamlFile.replace(".gaml", "");
            final String pathToXmlFile = "./storage/xmls/" + experimentId + "_" + fileName + ".xml";

            final String createXmlCommand = gamaCommandBuilder.createXmlFile(
                    experimentName,
                    "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/" + gamlFile,
                    pathToXmlFile
            );

            final String runLegacyCommand = gamaCommandBuilder.buildLegacy(
                    null,
                    pathToXmlFile,
                    "./storage/outputs/" + experimentId + "_" + fileName + "/"
            );

            gamaCommandExecutor.executeLegacy(
                    createXmlCommand,
                    runLegacyCommand,
                    pathToXmlFile,
                    experimentId,
                    experimentName,
                    finalStep
            );
        });
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
