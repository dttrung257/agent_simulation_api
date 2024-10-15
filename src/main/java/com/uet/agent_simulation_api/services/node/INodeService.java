package com.uet.agent_simulation_api.services.node;

import com.uet.agent_simulation_api.models.Node;
import org.springframework.web.reactive.function.client.WebClient;

public interface INodeService {
    Node getCurrentNode();

    /**
     * Get current node id
     *
     * @return nodeId Integer
     */
    Integer getCurrentNodeId();

    Node getNodeById(Integer nodeId);

    WebClient getWebClientByNodeId(Integer nodeId);
}