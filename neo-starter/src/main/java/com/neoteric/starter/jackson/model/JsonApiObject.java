package com.neoteric.starter.jackson.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@Builder
@JsonDeserialize(builder = JsonApiObject.JsonApiObjectBuilder.class)
public class JsonApiObject<T> {

    T data;
    @Singular("meta") Map<String, Object> meta;

    public static <T> JsonApiObject<T> wrap(T object) {
        return (JsonApiObject<T>) JsonApiObject.builder().data(object).build();
    }
}