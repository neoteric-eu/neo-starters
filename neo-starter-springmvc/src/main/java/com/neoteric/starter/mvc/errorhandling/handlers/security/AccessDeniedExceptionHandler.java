package com.neoteric.starter.mvc.errorhandling.handlers.security;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.FORBIDDEN, suppressException = true)
public class AccessDeniedExceptionHandler implements RestExceptionHandler<AccessDeniedException> {

    @Override
    public Object errorMessage(AccessDeniedException ex, HttpServletRequest request) {
        return ex.getMessage();
    }

}