package com.neoteric.starter.error;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundExceptionMapper extends AbstractExceptionMapper<ResourceNotFoundException> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceNotFoundExceptionMapper.class);

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
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
    protected Object message(ResourceNotFoundException resourceNotFoundException) {
        return resourceNotFoundException.getMessage();
    }
}