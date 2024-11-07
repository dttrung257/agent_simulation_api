package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.PigpenSyncRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PigpenSyncRunRepository extends JpaRepository<PigpenSyncRun, Integer> {
    @Query(value = "SELECT MAX(runId) FROM PigpenDaily")
    Integer findNewRunId();
}
