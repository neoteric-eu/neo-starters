package com.neoteric.starter.mvc.errorhandling.handlers.common;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.METHOD_NOT_ALLOWED)
public class HttpRequestMethodNotSupportedExceptionHandler implements RestExceptionHandler<HttpRequestMethodNotSupportedException> {

    public static final String ALLOW_HEADER = "Allow";

    @Override
    public Object errorMessage(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return ex.getMessage();
    }

    @Override
    public void customizeResponse(HttpRequestMethodNotSupportedException ex, HttpServletRequest req, HttpServletResponse resp) {
        String[] supportedMethods = ex.getSupportedMethods();
        if (supportedMethods != null) {
            resp.setHeader(ALLOW_HEADER, StringUtils.arrayToDelimitedString(supportedMethods, ", "));
        }
    }
}
