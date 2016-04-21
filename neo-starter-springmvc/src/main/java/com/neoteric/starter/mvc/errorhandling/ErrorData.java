package com.neoteric.starter.mvc.errorhandling;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.Map;

@Value
@Builder
@JsonDeserialize(builder = ErrorData.ErrorDataBuilder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData {

    ZonedDateTime timestamp;
    String requestId;
    String path;
    Integer status;
    String error;
    Object errorCode;
    String exception;
    Map<String, Object> additionalInfo;
    Object message;
    Map<String, String> stackTrace;

    @JsonAnyGetter
    Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

@JsonPOJOBuilder(withPrefix = "")
public static class ErrorDataBuilder {
}
}