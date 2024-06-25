package com.uet.agent_simulation_api.commands.executors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamaCommandExecutor implements IGamaCommandExecutor {
    private final ExecutorService virtualThreadExecutor;

    @Override
    public void execute(String command) {
        virtualThreadExecutor.submit(() -> {
            try {
                final Process process = (new ProcessBuilder()).command("bash", "-c", command).start();
                this.getCommandOutput(process);
            } catch (Exception e) {
                log.error("Error while executing command: {}", command, e);
            }
        });
    }

    @Override
    public void getCommandOutput(Process process) {
        // Output stream
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Error stream
        final BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            while ((line = errorReader.readLine()) != null) {
                line = "Error: " + line;
                log.error(line);
            }
        } catch (IOException e) {
            log.error("Error while reading command output, error: ", e);
        }
    }

    @Override
    public void executeLegacy(
            String createXmlCommand,
            String runLegacyCommand,
            String pathToXmlFile,
            int experimentId,
            String experimentName,
            long finalStep
    ) {
        virtualThreadExecutor.submit(() -> {
            try {
                if (createXmlCommand != null) {
                    log.info("Start creating xml file: {}", pathToXmlFile);
                    final Process createXmlProcess = (new ProcessBuilder()).command("bash", "-c", createXmlCommand).start();
                    this.getCommandOutput(createXmlProcess);

                    // Wait until create and update xml file done.
                    final int createXmlExitCode = createXmlProcess.waitFor();
                    if (createXmlExitCode != 0) {
                        log.error("createXmlCommand failed with exit code: {}", createXmlExitCode);
                        return;
                    }
                    log.info("Finish creating xml file: {}", pathToXmlFile);

                    // update experiment plan
                    log.info("Start updating experiment plan: {}", pathToXmlFile);
                    this.updateExperimentPlan(pathToXmlFile, experimentId, experimentName, finalStep);
                    log.info("Finish updating experiment plan: {}", pathToXmlFile);
                }

                if (runLegacyCommand != null) {
                    log.info("Start running legacy command: {}", runLegacyCommand);
                    final Process runLegacyProcess = (new ProcessBuilder()).command("bash", "-c", runLegacyCommand).start();
                    this.getCommandOutput(runLegacyProcess);
                    log.info("Finish running legacy command: {}", runLegacyCommand);
                }
            } catch (Exception e) {
                log.error("Error while executing legacy command: {}", runLegacyCommand, e);
            }
        });
    }

    private void updateExperimentPlan(
            String pathToXmlFile,
            int experimentId,
            String experimentName,
            long finalStep
    ) {
        try {
            // Read the file content into a string
            final StringBuilder content = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new FileReader(pathToXmlFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) { // Check if line is not empty or blank
                        content.append(line).append(System.lineSeparator());
                    }
                }
            }

            // Convert the content to a string
            String xmlContent = content.toString();

            // Replace the first occurrence of each attribute
            xmlContent = xmlContent.replaceFirst("experiment=\"[^\"]*\"", "experiment=\"" + experimentName + "\"");
            xmlContent = xmlContent.replaceFirst("finalStep=\"[^\"]*\"", "finalStep=\"" + finalStep + "\"");
            xmlContent = xmlContent.replaceFirst("id=\"[^\"]*\"", "id=\"" + experimentId + "\"");

            // Write the modified content back to the file
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(pathToXmlFile))) {
                writer.write(xmlContent.trim()); // Trim to remove extra new lines
            }

            log.info("Experiment plan updated successfully: {}", pathToXmlFile);
        } catch (IOException e) {
            log.error("Error while updating experiment plan: ", e);
        }
    }
}
