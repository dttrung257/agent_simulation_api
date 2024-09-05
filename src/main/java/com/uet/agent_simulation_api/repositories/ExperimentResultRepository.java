package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface ExperimentResultRepository extends JpaRepository<ExperimentResult, BigInteger> {
    List<ExperimentResult> findByExperimentId(BigInteger experimentId);

    @Query(
        value = """
            SELECT er.* FROM experiment_results as er
            JOIN experiments ON er.experiment_id = experiments.id
            JOIN models ON experiments.model_id = models.id
            JOIN projects ON models.project_id = projects.id
            WHERE projects.user_id = :user_id
            AND (:experiment_id IS NULL OR experiments.id = :experiment_id)
            AND (:model_id IS NULL OR models.id = :model_id)
            AND (:project_id IS NULL OR projects.id = :project_id)
        """,
        nativeQuery = true
    )
    List<ExperimentResult> find(
            @Param("user_id") BigInteger userId,
            @Param("experiment_id") BigInteger experimentId,
            @Param("model_id") BigInteger modelId,
            @Param("project_id") BigInteger projectId);
}