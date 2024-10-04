package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentResultImageErrors;
import com.uet.agent_simulation_api.exceptions.experiment_result_images.ExperimentResultImageNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExperimentResultImageExceptionHandler {
    /**
     * Handle ExperimentResultImageNotFoundException
     *
     * @param e ExperimentResultImageNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExperimentResultImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentResultImageNotFoundException(
            ExperimentResultImageNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        ExperimentResultImageErrors.E_ERI_0001.statusCode(),
                        ExperimentResultImageErrors.E_ERI_0001.errorCode(),
                        ExperimentResultImageErrors.E_ERI_0001.defaultMessage(),
                        e.getMessage()
                )
        );
    }
}
