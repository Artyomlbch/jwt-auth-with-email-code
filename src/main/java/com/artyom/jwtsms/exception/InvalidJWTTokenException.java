package com.artyom.jwtsms.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJWTTokenException extends AuthenticationException {
    public InvalidJWTTokenException(String message) {
        super(message);
    }
}
