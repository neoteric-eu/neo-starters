package com.neoteric.starter.mvc.errorhandling.resolver;

import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ErrorLogger {

    private static final String LOG_MESSAGE = "Exception mapped: ";

    private static final ImmutableMap<Level,LoggerFunction> LOGGERS = ImmutableMap.<Level, LoggerFunction>builder()
            .put(Level.ERROR, (logger, error) -> logger.error(LOG_MESSAGE, error))
            .put(Level.WARN, (logger, error) -> logger.warn(LOG_MESSAGE, error))
            .put(Level.INFO, (logger, error) -> logger.info(LOG_MESSAGE, error))
            .put(Level.DEBUG, (logger, error) -> logger.debug(LOG_MESSAGE, error))
            .put(Level.TRACE, (logger, error) -> logger.trace(LOG_MESSAGE, error))
            .build();

    public void log(ExceptionHandlerBinding binding, Exception ex) {
        LOGGERS.get(binding.getLogLevel()).log(binding.getLogger(), ex);
    }

    @FunctionalInterface
    private interface LoggerFunction {
        void log(Logger logger, Throwable error);
    }
}