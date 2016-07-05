package com.neoteric.starter.mvc.logging;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.neoteric.starter.jackson.model.JsonApiObject;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.StringJoiner;

public class ApiLogger {

    private static final ImmutableMap<Level, ApiLoggingFunction> LOGGERS = ImmutableMap.<Level, ApiLoggingFunction>builder()
            .put(Level.ERROR, (logger, payload) -> logger.error(payload.getMessage(), payload.getArgs()))
            .put(Level.WARN, (logger, payload) -> logger.warn(payload.getMessage(), payload.getArgs()))
            .put(Level.INFO, (logger, payload) -> logger.info(payload.getMessage(), payload.getArgs()))
            .put(Level.DEBUG, (logger, payload) -> logger.debug(payload.getMessage(), payload.getArgs()))
            .put(Level.TRACE, (logger, payload) -> logger.trace(payload.getMessage(), payload.getArgs()))
            .build();

    private final ApiLoggingProperties logProps;
    private final String resourceName;
    private final Logger logger;

    public ApiLogger(ApiLoggingProperties logProps, String resourceName, Logger logger) {
        this.logProps = logProps;
        this.resourceName = wrapResourceName(resourceName);
        this.logger = logger;
    }

    private String wrapResourceName(String resourceName) {
        if (StringUtils.isEmpty(resourceName)) {
            return resourceName;
        }
        StringJoiner joiner = new StringJoiner("", "[", "]");
        return joiner.add(resourceName).toString();
    }

    public void logEntryPoint(String methodName, String methodParams) {
        LogPayload payload = LogPayload.of("{}", methodName)
                .prepend(resourceName)
                .append(methodParams);
        LOGGERS.get(logProps.getEntryPointLevel()).log(logger, payload);
    }

    public void logCustomObjectDetails(String complexMethodParams) {
        LogPayload payload = LogPayload.of("Details: {}", complexMethodParams)
                .prepend(resourceName);
        LOGGERS.get(logProps.getCustomParamsLevel()).log(logger, payload);
    }

    public void logExitPoint(String methodName, String methodParams, double totalTimeSeconds) {
        LogPayload payload = LogPayload.of("took {} seconds", totalTimeSeconds)
                .prepend(methodParams)
                .prepend(methodName)
                .prepend(resourceName);
        LOGGERS.get(logProps.getExitPointLevel()).log(logger, payload);
    }

    public void logReturnedJsonApiListSize(int size) {
        LogPayload payload = LogPayload.of("Returning {} items", size)
                .prepend(resourceName);
        LOGGERS.get(logProps.getJsonApiListSizeLevel()).log(logger, payload);
    }

    public void logReturnedJsonApiObjectDetails(String jsonApiDetails) {
        LogPayload payload = LogPayload.of("Returning [{}]", jsonApiDetails)
                .prepend(resourceName);
        LOGGERS.get(logProps.getJsonApiObjectLevel()).log(logger, payload);
    }

    private static class LogPayload {
        private String message;
        private List<Object> args;

        public static LogPayload of(String message, List<Object> args) {
            return new LogPayload(message, args);
        }

        public static LogPayload of(String message, Object arg) {
            return of(message, Lists.newArrayList(arg));
        }

        private LogPayload(String message, List<Object> args) {
            this.message = message;
            this.args = args;
        }


        public LogPayload prepend(String value) {
            if (StringUtils.hasLength(value)) {
                message = String.join("", "{} ", message);
                args.add(0, value);
            }
            return this;
        }

        public String getMessage() {
            return String.join("", message, ".");
        }

        public Object[] getArgs() {
            return args.toArray();
        }

        public LogPayload append(String value) {
            if (StringUtils.hasLength(value)) {
                message = String.join("", message, " {}");
                args.add(args.size(), value);
            }
            return this;
        }
    }

    @FunctionalInterface
    private interface ApiLoggingFunction {
        void log(Logger logger, LogPayload logPayload);
    }
}
