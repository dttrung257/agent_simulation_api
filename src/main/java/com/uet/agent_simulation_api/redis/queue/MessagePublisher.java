package com.uet.agent_simulation_api.redis.queue;

public interface MessagePublisher {
    void publish(String message);
}
