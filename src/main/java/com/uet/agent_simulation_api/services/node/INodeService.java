package com.uet.agent_simulation_api.services.node;

import com.uet.agent_simulation_api.models.Node;
import com.uet.agent_simulation_api.responses.node.NodeBriefResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public interface INodeService {
    List<NodeBriefResponse> get();

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
