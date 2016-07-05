package com.neoteric.starter.mvc.logging;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.StringJoiner;

@AllArgsConstructor
public class ApiLogger {

    private static final ImmutableMap<Level, ApiLoggingFunction> LOGGERS = ImmutableMap.<Level, ApiLoggingFunction>builder()
            .put(Level.ERROR, (logger, message, args) -> logger.error(message, args))
            .put(Level.WARN, (logger, message, args) -> logger.warn(message, args))
            .put(Level.INFO, (logger, message, args) -> logger.info(message, args))
            .put(Level.DEBUG, (logger, message, args) -> logger.debug(message, args))
            .put(Level.TRACE, (logger, message, args) -> logger.trace(message, args))
            .build();

    private final ApiLoggingProperties logProps;
    private final String resourceName;
    private final Logger logger;

    public void logEntryPoint(String methodName, String methodParams) {
        LogPayload payload = new LogPayload("{}", methodName)
                .withResourceName(resourceName)
                .withParameters(methodParams);
        LOGGERS.get(logProps.getEntryPointLevel()).log(logger, payload.getMessage(), payload.getArgs());
    }

    public void logCustomObjectDetails(String complexMethodParams) {
        LogPayload payload = new LogPayload("Details: {}", complexMethodParams)
                .withResourceName(resourceName);
        LOGGERS.get(logProps.getCustomParamsLevel()).log(logger, payload.getMessage(), payload.getArgs());
    }

    public void logExitPoint(String methodName, String methodParams, double totalTimeSeconds) {

    }

    private static class LogPayload {
        private String message;
        private List<Object> args;

        public LogPayload(String message, List<Object> args) {
            this.message = message;
            this.args = args;
        }

        public LogPayload(String message, Object arg) {
            this(message, Lists.newArrayList(arg));
        }

        public LogPayload withResourceName(String resourceName) {
            if (StringUtils.hasLength(resourceName)) {
                message = "{}" + message;
                args.add(0, wrapResourceName(resourceName));
            }
            return this;
        }

        public String getMessage() {
            return message + ".";
        }

        public Object[] getArgs() {
            return args.toArray();
        }

        public LogPayload withParameters(String params) {
            if (params.length() > 2) {
                message = message + " {}";
                args.add(args.size(), params);
            }
            return this;
        }

        private String wrapResourceName(String resourceName) {
            StringJoiner joiner = new StringJoiner("", "[", "] ");
            return joiner.add(resourceName).toString();
        }
    }

    @FunctionalInterface
    private interface ApiLoggingFunction {
        void log(Logger logger, String message, Object[] args);
    }
}
