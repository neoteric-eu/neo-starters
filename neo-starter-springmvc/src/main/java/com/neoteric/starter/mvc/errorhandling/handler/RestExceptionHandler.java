package com.neoteric.starter.mvc.errorhandling.handler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface RestExceptionHandler<T extends Exception> {

    Object errorMessage(T exception, HttpServletRequest request);

    default Map<String, Object> additionalInfo(T exception, HttpServletRequest request) {
        return null;
    }

}