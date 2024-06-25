package com.uet.agent_simulation_api.commands.executors;

public interface IGamaCommandExecutor extends ICommandExecutor {
    /**
     * This method is used to execute the legacy mode gama headless.
     *
     * @param createXmlCommand - command to create xml file
     * @param runLegacyCommand - command to run legacy mode
     * @param pathToXmlFile - path to xml file
     * @param experimentId - id of the experiment
     * @param experimentName - name of the experiment
     * @param finalStep - final step of the experiment
     */
    void executeLegacy(String createXmlCommand, String runLegacyCommand, String pathToXmlFile, int experimentId, String experimentName, long finalStep);
}
