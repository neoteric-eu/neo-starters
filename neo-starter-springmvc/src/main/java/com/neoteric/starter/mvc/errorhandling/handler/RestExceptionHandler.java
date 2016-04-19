package com.neoteric.starter.mvc.errorhandling.handler;

import com.neoteric.starter.mvc.errorhandling.ErrorData;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface RestExceptionHandler<T extends Throwable> {

    ResponseEntity<ErrorData> handleException(T throwable, HttpServletRequest request);
}
