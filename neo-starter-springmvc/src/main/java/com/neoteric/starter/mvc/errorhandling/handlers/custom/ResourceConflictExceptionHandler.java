package com.neoteric.starter.mvc.errorhandling.handlers.custom;

import com.neoteric.starter.exception.ResourceConflictException;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.CONFLICT)
public class ResourceConflictExceptionHandler implements RestExceptionHandler<ResourceConflictException> {

    @Override
    public Object errorMessage(ResourceConflictException exception, HttpServletRequest request) {
        return exception.getMessage();
    }
}
