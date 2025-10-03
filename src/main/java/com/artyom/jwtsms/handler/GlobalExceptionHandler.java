package com.artyom.jwtsms.handler;

import com.artyom.jwtsms.dto.ExceptionResponse;
import com.artyom.jwtsms.exception.EmailAlreadyExistsException;
import com.artyom.jwtsms.exception.InvalidJWTTokenException;
import com.artyom.jwtsms.exception.UserNotFoundException;
import com.artyom.jwtsms.exception.VerificationException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .details("This email is already in use")
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();

    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleExpiredJwtException(ExpiredJwtException ex) {
        return ExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .details("JWT token is expired")
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleRuntimeException(RuntimeException ex) {
        return ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .details("Something went wrong")
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleBadCredentialsException(BadCredentialsException ex) {
        return ExceptionResponse.builder()
                .message(ex.getMessage())
                .details("Wrong username or password")
                .timestamp(new Date())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();
    }

    @ExceptionHandler(InvalidJWTTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleInvalidJWTTokenException(InvalidJWTTokenException ex) {
        return ExceptionResponse.builder()
                .message(ex.getMessage())
                .details("Invalid JWT token")
                .timestamp(new Date())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();
    }

}
