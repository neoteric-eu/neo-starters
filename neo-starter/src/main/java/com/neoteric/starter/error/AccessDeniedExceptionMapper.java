package com.neoteric.starter.error;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

public class AccessDeniedExceptionMapper extends AbstractExceptionMapper<AccessDeniedException> {

    private static final Logger LOG = LoggerFactory.getLogger(AccessDeniedExceptionMapper.class);

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    protected Logger logger() {
        return LOG;
    }

    @Override
    protected Level logLevel() {
        return Level.ERROR;
    }

    @Override
    protected Object message(AccessDeniedException accessDeniedException) {
        return accessDeniedException.getMessage();
    }
}