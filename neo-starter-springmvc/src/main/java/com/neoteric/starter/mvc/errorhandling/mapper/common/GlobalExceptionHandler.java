package com.neoteric.starter.mvc.errorhandling.mapper.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;

import javax.servlet.http.HttpServletRequest;

public class GlobalExceptionHandler implements RestExceptionHandler<Exception> {

    @Override
    public Object errorMessage(Exception throwable, HttpServletRequest request) {
        return "w00t";
    }
}
