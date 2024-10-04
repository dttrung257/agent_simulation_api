package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, BigInteger> {
    @Query(
        value = """
            SELECT * FROM models
            WHERE id = :model_id AND project_id = :project_id
        """,
        nativeQuery = true
    )
    Optional<Model> findByModeIdAndProjectId(@Param("model_id") BigInteger modelId, @Param("project_id") BigInteger projectId);

    @Query(
        value = """
            SELECT m.* FROM models as m
            WHERE m.user_id = :user_id
            AND (:project_id IS NULL OR m.project_id = :project_id)
        """,
        nativeQuery = true
    )
    List<Model> find(@Param("user_id") BigInteger userId, @Param("project_id") BigInteger projectId);

    @Query(
        value = """
            SELECT m.* FROM models as m
            JOIN experiments ON m.id = experiments.model_id
            WHERE m.user_id = :user_id
            AND (:project_id IS NULL OR m.project_id = :project_id)
            AND experiments.id IS NOT NULL
        """,
        nativeQuery = true
    )
    List<Model> findByExperimentNotNull(@Param("user_id") BigInteger userId, @Param("project_id") BigInteger projectId);
}
