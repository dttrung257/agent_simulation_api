package com.uet.agent_simulation_api.responses;

/**
 * SuccessResponse class
 */
public record SuccessResponse(
        String status,
        int statusCode,
        Object data
) {
}
