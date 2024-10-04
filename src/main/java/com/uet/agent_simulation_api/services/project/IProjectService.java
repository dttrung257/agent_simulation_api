package com.uet.agent_simulation_api.services.project;

import com.uet.agent_simulation_api.models.Project;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface IProjectService {
    List<Project> get();

    Optional<Project> getProject(BigInteger id);
}
