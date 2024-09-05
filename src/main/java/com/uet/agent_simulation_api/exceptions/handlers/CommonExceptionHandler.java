package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.constant.AppConst;
import com.uet.agent_simulation_api.exceptions.auth.UnauthorizedException;
import com.uet.agent_simulation_api.exceptions.errors.CommonErrors;
import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/*
 * This class is used to handle common exceptions.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {
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
                    CommonErrors.E0001.statusCode(),
                    CommonErrors.E0001.errorCode(),
                    e.getMessage(),
                    e.getStackTrace()
            );

            // If the profile is not dev or local, return a generic error message.
            default -> new ErrorResponse(
                    AppConst.ERROR,
                    CommonErrors.E0001.statusCode(),
                    CommonErrors.E0001.errorCode(),
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
                        CommonErrors.E0002.statusCode(),
                        CommonErrors.E0002.errorCode(),
                        CommonErrors.E0002.defaultMessage(),
                        errors
                )
        );
    }

    /*
     * This method is used to handle authentication exception.
     * Error code: E0003 - Unauthorized
     * Status code: 401 - Unauthorized
     *
     * @param e AuthenticationException
     *
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        CommonErrors.E0003.statusCode(),
                        CommonErrors.E0003.errorCode(),
                        CommonErrors.E0003.defaultMessage(),
                        CommonErrors.E0003.defaultMessage()
                )
        );
    }

    /*
     * This method is used to handle access denied exception.
     * Error code: E0004 - Access Denied
     * Status code: 403 - Forbidden
     *
     * @param e AccessDeniedException
     *
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        CommonErrors.E0004.statusCode(),
                        CommonErrors.E0004.errorCode(),
                        CommonErrors.E0004.defaultMessage(),
                        e.getMessage()
                )
        );
    }

    /*
     * This method is used to handle invalid token exception.
     * Error code: E0003 - Unauthorized
     * Status code: 401 - Unauthorized
     *
     * @param e InvalidTokenException
     *
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(UnauthorizedException e) {
        final var detail = switch (profile) {
            // dev or local
            case "dev", "local" -> e.getMessage();

            // production (do not show the error detail to security reasons)
            default -> null;
        };

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        CommonErrors.E0003.statusCode(),
                        CommonErrors.E0003.errorCode(),
                        CommonErrors.E0003.defaultMessage(),
                        detail
                )
        );
    }

    /*
     * This method is used to handle login fail (UsernameNotFoundException) exception.
     * Error code: E0005
     * Status code: 401 - Unauthorized
     *
     * @param e UsernameNotFoundException
     *
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        CommonErrors.E0005.statusCode(),
                        CommonErrors.E0005.errorCode(),
                        CommonErrors.E0005.defaultMessage(),
                        e.getMessage()
                )
        );
    }


    /*
     * This method is used to handle login fail (BadCredentialsException) exception.
     * Error code: E0005
     * Status code: 401 - Unauthorized
     *
     * @param e BadCredentialsException
     *
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(
                        AppConst.ERROR,
                        CommonErrors.E0005.statusCode(),
                        CommonErrors.E0005.errorCode(),
                        CommonErrors.E0005.defaultMessage(),
                        e.getMessage()
                )
        );
    }
}
