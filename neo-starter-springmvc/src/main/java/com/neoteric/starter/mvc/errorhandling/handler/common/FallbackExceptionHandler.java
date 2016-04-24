package com.neoteric.starter.mvc.errorhandling.handler.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider
public class FallbackExceptionHandler implements RestExceptionHandler<Exception> {

    public static final String FALLBACK_ERROR_MSG = "Unknown error";

    @Override
    public Object errorMessage(Exception throwable, HttpServletRequest request) {
        return FALLBACK_ERROR_MSG;
    }
}
