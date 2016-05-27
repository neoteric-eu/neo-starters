package com.neoteric.starter.mvc.errorhandling.handlers.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.BAD_REQUEST)
public class IllegalArgumentExceptionHandler implements RestExceptionHandler<IllegalArgumentException> {

    @Override
    public Object errorMessage(IllegalArgumentException exception, HttpServletRequest request) {
        return exception.getMessage();
    }
}
