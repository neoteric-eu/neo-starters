package com.neoteric.starter.jersey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;


//TODO: Should be obsolete in Spring Boot 1.4.0 release: https://github.com/spring-projects/spring-boot/issues/4131
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}