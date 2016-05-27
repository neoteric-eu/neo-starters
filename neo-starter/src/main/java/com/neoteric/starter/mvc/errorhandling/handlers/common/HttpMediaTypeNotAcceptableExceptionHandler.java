package com.neoteric.starter.mvc.errorhandling.handlers.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.NOT_ACCEPTABLE)
public class HttpMediaTypeNotAcceptableExceptionHandler implements RestExceptionHandler<HttpMediaTypeNotAcceptableException> {
    @Override
    public Object errorMessage(HttpMediaTypeNotAcceptableException ex, HttpServletRequest req) {
        return ex.getMessage();
    }
}
