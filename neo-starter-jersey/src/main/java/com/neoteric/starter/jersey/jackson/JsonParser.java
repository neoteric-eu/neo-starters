package com.neoteric.starter.jersey.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonParser {

    private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);

    private final ObjectMapper mapper;

    public JsonParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to serialize to JSON", e);
            throw new IllegalStateException("Unable to serialize to JSON", e);
        }
    }

    public <T> T fromJson(Class<T> resultClass, String json) {
        try {
            return mapper.readValue(json, resultClass);
        } catch (IOException e) {
            LOG.error("Unable to deserialize JSON to {}", resultClass.getSimpleName(), e);
            throw new IllegalArgumentException("Unable to deserialize JSON to " + resultClass.getSimpleName());
        }
    }
}