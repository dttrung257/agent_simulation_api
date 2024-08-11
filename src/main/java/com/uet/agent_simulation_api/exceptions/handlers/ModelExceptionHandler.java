package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ModelExceptionHandler {
    /**
     * Handle ModelNotFoundException
     *
     * @param e ModelNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleModelNotFoundException(ModelNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        ModelErrors.E_MODEL_0001.statusCode(),
                        ModelErrors.E_MODEL_0001.errorCode(),
                        ModelErrors.E_MODEL_0001.defaultMessage(),
                        e.getMessage()
                )
        );
    }
}
