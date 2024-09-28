package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResultImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface ExperimentResultImageRepository extends JpaRepository<ExperimentResultImage, BigInteger> {
    List<ExperimentResultImage> findByExperimentResultId(BigInteger experimentResultId);

    @Query(
        value = """
            SELECT eri.* FROM experiment_result_images as eri
            JOIN experiment_results ON eri.experiment_result_id = experiment_results.id
            JOIN experiments ON experiment_results.experiment_id = experiments.id
            JOIN models ON experiments.model_id = models.id
            JOIN projects ON models.project_id = projects.id
            WHERE projects.user_id = :user_id
            AND (:experiment_result_id IS NULL OR experiment_results.id = :experiment_result_id)
            AND (:experiment_id IS NULL OR experiments.id = :experiment_id)
            AND (:model_id IS NULL OR models.id = :model_id)
            AND (:project_id IS NULL OR projects.id = :project_id)
        """,
        nativeQuery = true
    )
    List<ExperimentResultImage> find(
            @Param("user_id") BigInteger userId,
            @Param("experiment_result_id") BigInteger experimentResultId,
            @Param("experiment_id") BigInteger experimentId,
            @Param("model_id") BigInteger modelId,
            @Param("project_id") BigInteger projectId);

    @Transactional
    @Modifying
    @Query("delete from ExperimentResultImage eri where eri.experimentResultId = :experiment_result_id")
    void deleteByExperimentResultId(@Param("experiment_result_id") BigInteger experimentResultId);
}
