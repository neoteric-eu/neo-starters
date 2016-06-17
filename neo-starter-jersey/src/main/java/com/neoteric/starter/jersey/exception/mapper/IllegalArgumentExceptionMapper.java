package com.neoteric.starter.jersey.exception.mapper;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper extends AbstractExceptionMapper<IllegalArgumentException> {

    private static final Logger LOG = LoggerFactory.getLogger(IllegalArgumentExceptionMapper.class);

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
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
    protected Object message(IllegalArgumentException illegalArgumentException) {
        return illegalArgumentException.getMessage();
    }
}