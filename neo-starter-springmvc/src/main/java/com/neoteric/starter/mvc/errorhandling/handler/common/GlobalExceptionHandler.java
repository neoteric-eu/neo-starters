package com.neoteric.starter.mvc.errorhandling.handler.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider
public class GlobalExceptionHandler implements RestExceptionHandler<Exception> {

    @Override
    public Object errorMessage(Exception throwable, HttpServletRequest request) {
        return "w00t";
    }
}
