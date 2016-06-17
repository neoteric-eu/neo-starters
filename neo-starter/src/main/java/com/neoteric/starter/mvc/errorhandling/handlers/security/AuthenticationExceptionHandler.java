package com.neoteric.starter.mvc.errorhandling.handlers.security;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.UNAUTHORIZED, suppressException = true)
public class AuthenticationExceptionHandler implements RestExceptionHandler<AuthenticationException> {

    @Override
    public Object errorMessage(AuthenticationException ex, HttpServletRequest request) {
        return ex.getMessage();
    }

}