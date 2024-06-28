package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserRepository extends JpaRepository<AppUser, BigInteger> {
}
