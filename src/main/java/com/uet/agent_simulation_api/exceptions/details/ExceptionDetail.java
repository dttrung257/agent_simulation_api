package com.uet.agent_simulation_api.exceptions.details;

public record ExceptionDetail(
    String status,
    int statusCode,
    String timestamp,
    String message,
    Object detail
) {
}
