package com.uet.agent_simulation_api.services.impl;

import com.uet.agent_simulation_api.services.IAuthService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class AuthService implements IAuthService {
    public BigInteger getCurrentUserId() {
        return BigInteger.valueOf(1);
    }
}
