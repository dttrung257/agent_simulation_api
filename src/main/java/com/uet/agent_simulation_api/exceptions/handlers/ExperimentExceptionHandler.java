package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentErrors;
import com.uet.agent_simulation_api.exceptions.experiment.ExperimentNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExperimentExceptionHandler {
    /**
     * Handle ExperimentNotFoundException
     *
     * @param e ExperimentNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExperimentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentNotFoundException(ExperimentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        ExperimentErrors.E_EXP_0001.statusCode(),
                        ExperimentErrors.E_EXP_0001.errorCode(),
                        ExperimentErrors.E_EXP_0001.defaultMessage(),
                        e.getMessage()
                )
        );
    }
}
