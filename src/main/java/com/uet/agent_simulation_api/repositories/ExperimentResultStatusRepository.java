package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface ExperimentResultStatusRepository extends JpaRepository<ExperimentResultStatus, BigInteger> {
    @Query(
        value = """
            SELECT ers.* FROM experiment_result_statuses as ers
            JOIN experiments ON ers.experiment_id = experiments.id
            JOIN models ON experiments.model_id = models.id
            JOIN projects ON models.project_id = projects.id
            WHERE projects.user_id = :user_id
            AND (:experiment_id IS NULL OR ers.experiment_id = :experiment_id)
        """,
        nativeQuery = true
    )
    List<ExperimentResultStatus> findByUserIdAndExperimentId(@Param("user_id") BigInteger userId, @Param("experiment_id") BigInteger experimentId);
}