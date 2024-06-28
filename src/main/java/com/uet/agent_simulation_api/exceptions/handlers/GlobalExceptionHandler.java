package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.errors.CommonError;
import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

/*
 * This class is used to handle common exceptions.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final TimeUtil timeUtil;

    @Value("${spring.profiles.active}")
    private String profile;

    /*
     * This method is used to handle global exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        final ErrorResponse exceptionDetail = switch (profile) {
            // If the profile is dev or local, return the stack trace.
            case "dev", "local" -> new ErrorResponse(
                    AppConst.ERROR,
                    CommonError.E0001.statusCode(),
                    CommonError.E0001.errorCode(),
                    e.getMessage(),
                    e.getStackTrace()
            );

            // If the profile is not dev or local, return a generic error message.
            default -> new ErrorResponse(
                    AppConst.ERROR,
                    CommonError.E0001.statusCode(),
                    CommonError.E0001.errorCode(),
                    "Something went wrong",
                    null
            );
        };

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDetail);
    }

    /*
     * This method is used to handle data validation exception.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        CommonError.E0002.statusCode(),
                        CommonError.E0002.errorCode(),
                        CommonError.E0002.defaultMessage(),
                        errors
                )
        );
    }
}
