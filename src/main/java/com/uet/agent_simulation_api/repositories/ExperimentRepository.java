package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ExperimentRepository extends JpaRepository<Experiment, BigInteger> {
    @Query(
        value = """
        SELECT * FROM experiments
        WHERE id = :experiment_id AND model_id = :model_id
        """,
        nativeQuery = true
    )
    Optional<Experiment> findByExperimentIdAndModelId(@Param("experiment_id") BigInteger experimentId, @Param("model_id") BigInteger modelId);

    @Query(
        value = """
            SELECT e.* FROM experiments as e
            JOIN models ON e.model_id = models.id
            JOIN projects ON models.project_id = projects.id
            WHERE projects.user_id = :user_id
            AND (:project_id IS NULL OR projects.id = :project_id)
            AND (:model_id IS NULL OR models.id = :model_id)
        """,
        nativeQuery = true
    )
    List<Experiment> find(@Param("user_id") BigInteger userId, @Param("project_id") BigInteger projectId, @Param("model_id") BigInteger modelId);
}
