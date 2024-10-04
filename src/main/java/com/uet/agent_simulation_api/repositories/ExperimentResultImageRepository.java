package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResultImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ExperimentResultImageRepository extends JpaRepository<ExperimentResultImage, BigInteger> {
    List<ExperimentResultImage> findByExperimentResultId(BigInteger experimentResultId);

    @Query(
        value = """
            SELECT eri FROM ExperimentResultImage eri
            JOIN ExperimentResult er ON eri.experimentResultId = er.id
            JOIN Experiment e ON er.experimentId = e.id
            WHERE e.userId = :user_id
            AND (:experiment_result_id IS NULL OR er.id = :experiment_result_id)
            AND (:experiment_id IS NULL OR e.id = :experiment_id)
            AND (:model_id IS NULL OR e.modelId = :model_id)
            AND (:project_id IS NULL OR e.projectId = :project_id)
            AND (:experiment_result_category_id IS NULL OR eri.experimentResultCategoryId = :experiment_result_category_id)
            AND (:step IS NULL OR eri.step = :step)
        """
    )
    Page<ExperimentResultImage> find(@Param("user_id") BigInteger userId,
        @Param("experiment_result_id") BigInteger experimentResultId,
        @Param("experiment_id") BigInteger experimentId,
        @Param("model_id") BigInteger modelId,
        @Param("project_id") BigInteger projectId,
        @Param("experiment_result_category_id") BigInteger experimentResultCategoryId,
        @Param("step") Integer step,
        Pageable pageable);

    @Query(
        value = """
            SELECT eri.* FROM experiment_result_images as eri
            JOIN experiment_results ON eri.experiment_result_id = experiment_results.id
            JOIN experiments ON experiment_results.experiment_id = experiments.id
            WHERE eri.id = :id
            AND experiments.user_id = :user_id
            LIMIT 1
        """,
        nativeQuery = true
    )
    Optional<ExperimentResultImage> findByIdAndUserId(@Param("id") BigInteger id, @Param("user_id") BigInteger userId);

    @Transactional
    @Modifying
    @Query("delete from ExperimentResultImage eri where eri.experimentResultId = :experiment_result_id")
    void deleteByExperimentResultId(@Param("experiment_result_id") BigInteger experimentResultId);
}
