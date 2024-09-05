package com.uet.agent_simulation_api;

import com.uet.agent_simulation_api.models.Node;
import com.uet.agent_simulation_api.repositories.NodeRepository;
import com.uet.agent_simulation_api.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
@Slf4j
public class AgentSimulationApiApplication implements CommandLineRunner {
	private final FileUtil fileUtil;
	private final NodeRepository nodeRepository;

	@Value("${cluster.config.path}")
	private String clusterConfigPath;

	public static void main(String[] args) {
		SpringApplication.run(AgentSimulationApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("CLuster config path: {}", clusterConfigPath);
		if (!existsClusterConfig()) {
			// exit program
			log.error(" not found");
			return;
		}

		if (!checkNodeId()) {
			// exit program
			log.error("Error while checking node ID");
			return;
		}

		initNode();
	}

	/**
	 * Check if the application.yml file exists.
	 *
	 * @return true if the file exists, false otherwise.
	 */
	private boolean existsClusterConfig() {
		return fileUtil.fileExists(clusterConfigPath);
	}

	/**
	 * Check if the node ID is valid.
	 *
	 * @return true if the node ID is valid, false otherwise.
	 */
	private boolean checkNodeId() {
		final var nodeId = fileUtil.getValueByKey(clusterConfigPath, "node_id");
		if (nodeId == null) {
			log.error("An error occurred while getting the node ID");

			return false;
		}

		if (nodeId.isEmpty()) {
			log.info("Node ID undefined in application.yml. Start setting node ID");

			fileUtil.findAndWrite(clusterConfigPath, "node_id", "1");
			fileUtil.findAndWrite(clusterConfigPath, "node_role", "1");
			return true;
		}

		try {
			Integer.parseInt(nodeId);
		} catch (NumberFormatException e) {
			log.error("Invalid node ID in application.yml. Node ID must be an integer");

			return false;
		}

		return true;
	}

	/**
	 * Initialize the node.
	 */
	private void initNode() {
		final var node = nodeRepository.findById(1);
		if (node.isEmpty()) {
			nodeRepository.save(Node.builder().id(1).name("master").role(1).build());
		}
	}
}
