package com.neoteric.starter.jersey.exception.mapper;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper extends AbstractExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);
    private static final String GLOBAL_ERROR_MSG = "Unknown error";

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
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
    protected Object message(Throwable throwable) {
        return GLOBAL_ERROR_MSG;
    }
}