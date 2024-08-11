package com.uet.agent_simulation_api.services;

import com.uet.agent_simulation_api.models.Model;

import java.math.BigInteger;
import java.util.List;

public interface IModelService {
    List<Model> get(BigInteger projectId);
}
