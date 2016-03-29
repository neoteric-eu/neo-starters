package com.neoteric.starter.exception.mapper;

import ch.qos.logback.classic.Level;
import com.neoteric.starter.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.ext.Provider;

@Provider
public class ResourceConflictExceptionMapper extends AbstractExceptionMapper<ResourceNotFoundException> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceNotFoundExceptionMapper.class);

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
    protected Object message(ResourceNotFoundException resourceNotFoundException) {
        return resourceNotFoundException.getMessage();
    }
}