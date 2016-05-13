package com.neoteric.starter.exception.mapper;

import ch.qos.logback.classic.Level;
import com.neoteric.starter.exception.ResourceConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ResourceConflictExceptionMapper extends AbstractExceptionMapper<ResourceConflictException> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceConflictExceptionMapper.class);

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
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
    protected Object message(ResourceConflictException ex) {
        return ex.getMessage();
    }
}
