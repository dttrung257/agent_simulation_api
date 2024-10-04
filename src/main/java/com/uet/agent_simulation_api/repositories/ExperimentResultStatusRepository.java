package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface ExperimentResultStatusRepository extends JpaRepository<ExperimentResultStatus, BigInteger> {
    @Query(
        value = """
            SELECT ers.* FROM experiment_result_statuses as ers
            JOIN experiments ON ers.experiment_id = experiments.id
            WHERE experiments.user_id = :user_id
            AND (:experiment_id IS NULL OR ers.experiment_id = :experiment_id)
        """,
        nativeQuery = true
    )
    List<ExperimentResultStatus> findByUserIdAndExperimentId(@Param("user_id") BigInteger userId, @Param("experiment_id") BigInteger experimentId);

    @Transactional
    @Modifying
    @Query("delete from ExperimentResultStatus ers where ers.experimentId = :experiment_id")
    void deleteByExperimentId(@Param("experiment_id") BigInteger experimentId);
}
