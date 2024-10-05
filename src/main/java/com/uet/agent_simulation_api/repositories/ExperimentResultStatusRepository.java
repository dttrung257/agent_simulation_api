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
            SELECT ers FROM ExperimentResultStatus ers
            JOIN Experiment e ON ers.experimentId = e.id
            WHERE e.userId = :user_id
            AND (:experiment_id IS NULL OR ers.experimentId = :experiment_id)
        """
    )
    List<ExperimentResultStatus> findByUserIdAndExperimentId(@Param("user_id") BigInteger userId,
        @Param("experiment_id") BigInteger experimentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ExperimentResultStatus ers WHERE ers.experimentId = :experiment_id")
    void deleteByExperimentId(@Param("experiment_id") BigInteger experimentId);
}
