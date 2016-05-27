package com.neoteric.starter.mvc.errorhandling.handlers.custom;

import com.neoteric.starter.exception.ResourceNotFoundException;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.NOT_FOUND)
public class ResourceNotFoundExceptionHandler implements RestExceptionHandler<ResourceNotFoundException> {

    @Override
    public Object errorMessage(ResourceNotFoundException exception, HttpServletRequest request) {
        return exception.getMessage();
    }
}