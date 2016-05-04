package com.neoteric.starter.mvc.errorhandling.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface RestExceptionHandler<T extends Exception> {

    Object errorMessage(T ex, HttpServletRequest req);

    default Map<String, Object> additionalInfo(T ex, HttpServletRequest req) {
        return null;
    }

    default void customizeResponse(T ex, HttpServletRequest req, HttpServletResponse resp) {
    }
}