package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface ExperimentResultRepository extends JpaRepository<ExperimentResult, BigInteger> {
    List<ExperimentResult> findByExperimentId(BigInteger experimentId);

    @Query(
        value = """
            SELECT er.* FROM experiment_results as er
            JOIN experiments ON er.experiment_id = experiments.id
            WHERE experiments.user_id = :user_id
            AND (:experiment_id IS NULL OR experiments.id = :experiment_id)
            AND (:model_id IS NULL OR experiments.model_id = :model_id)
            AND (:project_id IS NULL OR experiments.project_id = :project_id)
        """,
        nativeQuery = true
    )
    List<ExperimentResult> find(@Param("user_id") BigInteger userId,
            @Param("experiment_id") BigInteger experimentId,
            @Param("model_id") BigInteger modelId,
            @Param("project_id") BigInteger projectId);

    @Transactional
    @Modifying
    @Query("delete from ExperimentResult er where er.experimentId = :experiment_id")
    void deleteByExperimentId(@Param("experiment_id") BigInteger experimentId);
}
