package com.neoteric.starter.mvc.errorhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.Map;

@Value
@Builder
@JsonDeserialize(builder = com.neoteric.starter.exception.ErrorData.ErrorDataBuilder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData {

    ZonedDateTime timestamp;
    String requestId;
    Integer status;
    String error;
    Object errorCode;
    String path;
    Object message;
    String exception;
    Map<String, String> stackTrace;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ErrorDataBuilder {
    }
}