package com.neoteric.starter.jersey.exception.mapper;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class NotFoundExceptionMapper extends AbstractExceptionMapper<NotFoundException> {

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
    protected Object message(NotFoundException e) {
        return e.getMessage();
    }
}
