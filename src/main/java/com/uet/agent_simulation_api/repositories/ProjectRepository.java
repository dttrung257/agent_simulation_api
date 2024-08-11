package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, BigInteger> {
    @Query(
        value = """
            SELECT * FROM projects
            WHERE user_id = :user_id
        """,
        nativeQuery = true
    )
    List<Project> find(@Param("user_id") BigInteger userId);
}
