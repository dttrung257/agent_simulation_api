package com.uet.agent_simulation_api.commands;

import java.util.Map;

public interface IGamaCommandBuilder extends ICommandBuilder {
    /**
     * This method is used to build gama headless legacy command.
     *
     * @param options - Map<String, String>
     * @param pathToXmlFile - String
     * @param pathToOutputDir - String
     * @return String
     */
    String buildLegacy(Map<String, String> options, String pathToXmlFile, String pathToOutputDir);

    /**
     * This method is used to build gama headless batch command.
     *
     * @param options - Map<String, String>
     * @param experimentName - String
     * @param pathToGamlFile - String
     * @return String
     */
    String buildBatch(Map<String, String> options, String experimentName, String pathToGamlFile);
}
