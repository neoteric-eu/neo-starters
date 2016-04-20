package com.neoteric.starter.mvc.errorhandling.mapper.common;

import com.neoteric.starter.mvc.errorhandling.ErrorData;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(logLevel = Level.DEBUG, httpStatus = HttpStatus.FORBIDDEN)
public class GlobalExceptionMapper implements RestExceptionHandler<Throwable> {

    @Override
    public ErrorData handleException(Throwable throwable, HttpServletRequest request) {
        return null;

    }
}
