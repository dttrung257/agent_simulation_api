package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeRepository extends JpaRepository<Node, Integer> {
    @Query(value = "SELECT MAX(id) FROM nodes", nativeQuery = true)
    Integer findMaxId();
}
