package com.neoteric.starter.mvc.errorhandling.handler;

import javax.servlet.http.HttpServletRequest;

public interface RestExceptionHandler<T extends Throwable> {

    Object handleException(T throwable, HttpServletRequest request);
}