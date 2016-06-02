package com.neoteric.starter.mvc.errorhandling.resolver;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Maps;
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
    String exception;
    String applicationCode;
    @JsonIgnore
    Map<String, Object> additionalInfo;
    Object message;
    @JsonIgnore
    Map<String, String> stackTrace;

    // Workaround to always return stacktrace at the end
    @JsonAnyGetter
    public Map<String, Object> getAdditionalInfo() {
        if (additionalInfo == null && stackTrace == null) {
            return null;
        }
        Map<String, Object> additional = Maps.newLinkedHashMap();
        if (additionalInfo != null) {
            additional.putAll(additionalInfo);
        }
        if (stackTrace != null) {
            additional.put("stackTrace", stackTrace);
        }
        return additional;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ErrorDataBuilder {
    }
}