package com.neoteric.starter.jersey.exception.mapper;

import ch.qos.logback.classic.Level;
import org.glassfish.jersey.server.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.ext.Provider;

@Provider
public class QueryParamExceptionMapper extends AbstractExceptionMapper<ParamException.QueryParamException> {

    private static final Logger LOG = LoggerFactory.getLogger(QueryParamExceptionMapper.class);

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
    protected Object message(ParamException.QueryParamException exception) {
        return exception.getCause().getMessage();
    }
}
