package com.neoteric.starter.mvc.errorhandling.handlers.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.BAD_REQUEST, logLevel = Level.ERROR)
public class HttpMessageNotReadableExceptionHandler implements RestExceptionHandler<HttpMessageNotReadableException> {

    @Override
    public Object errorMessage(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return "Unable to read request body. May be missing.";
    }
}