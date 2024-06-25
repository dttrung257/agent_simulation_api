package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.details.ExceptionDetail;
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
    public ResponseEntity<ExceptionDetail> handleGlobalException(Exception e) {
        ExceptionDetail exceptionDetail = switch (profile) {
            // If the profile is dev or local, return the stack trace.
            case "dev", "local" -> new ExceptionDetail(
                    AppConst.ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    timeUtil.getCurrentTimeString(),
                    e.getMessage(),
                    e.getStackTrace()
            );

            // If the profile is not dev or local, return a generic error message.
            default -> new ExceptionDetail(
                    AppConst.ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    timeUtil.getCurrentTimeString(),
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
    public ResponseEntity<ExceptionDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionDetail(
                        AppConst.ERROR,
                        HttpStatus.BAD_REQUEST.value(),
                        timeUtil.getCurrentTimeString(),
                        "Validation failed",
                        errors
                )
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionDetail> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ExceptionDetail(
                        AppConst.ERROR,
                        HttpStatus.NOT_FOUND.value(),
                        timeUtil.getCurrentTimeString(),
                        e.getMessage(),
                        null
                )
        );
    }
}
