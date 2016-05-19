package com.neoteric.starter.mvc.errorhandling.handlers.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.NOT_FOUND)
public class NoHandlerFoundExceptionHandler implements RestExceptionHandler<NoHandlerFoundException> {

    @Override
    public Object errorMessage(NoHandlerFoundException exception, HttpServletRequest request) {
        return "URI not available";
    }
}