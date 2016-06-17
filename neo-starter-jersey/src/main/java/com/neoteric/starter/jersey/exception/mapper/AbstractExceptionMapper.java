package com.neoteric.starter.jersey.exception.mapper;

import ch.qos.logback.classic.Level;
import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.jersey.StarterConstants;
import com.neoteric.starter.jersey.exception.ErrorData;
import org.apache.catalina.connector.RequestFacade;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    @Autowired
    Clock clock;

    private static final String LOG_MESSAGE = "Exception mapped: ";

    private static final ImmutableMap<Level, LoggerFunction> LOGGERS = ImmutableMap.<Level, LoggerFunction>builder()
            .put(Level.ERROR, (logger, error) -> logger.error(LOG_MESSAGE, error))
            .put(Level.WARN, (logger, error) -> logger.warn(LOG_MESSAGE, error))
            .put(Level.INFO, (logger, error) -> logger.info(LOG_MESSAGE, error))
            .put(Level.DEBUG, (logger, error) -> logger.debug(LOG_MESSAGE, error))
            .put(Level.TRACE, (logger, error) -> logger.trace(LOG_MESSAGE, error))
            .build();

    protected abstract HttpStatus httpStatus();
    protected abstract Logger logger();
    protected abstract Level logLevel();
    protected abstract Object message(E throwable);
    protected Object errorCode(E throwable) {
        return null;
    }

    @FunctionalInterface
    private interface LoggerFunction {
        void log(Logger logger, Throwable error);
    }


    @Autowired
    ServerProperties serverProperties;

    @Override
    public Response toResponse(E error) {
        logError(error);
        Optional<RequestFacade> requestFacadeOptional = getRequestFacade();
        ErrorData.ErrorDataBuilder errorBuilder = ErrorData
                .builder()
                .timestamp(ZonedDateTime.now(clock))
                .requestId(String.valueOf(MDC.get(StarterConstants.REQUEST_ID)))
                .status(httpStatus().value())
                .error(httpStatus().getReasonPhrase())
                .message(message(error))
                .errorCode(errorCode(error))
                .exception(error.getClass().getName());

        if (requestFacadeOptional.isPresent()) {
            addPath(errorBuilder, requestFacadeOptional.get());
        }
        if (shouldIncludeStackTrace(serverProperties.getError(), requestFacadeOptional)) {
            addStackTrace(errorBuilder, error);
        }
        return Response
                .status(httpStatus().value())
                .entity(errorBuilder.build())
                .build();
    }

    private void logError(E error) {
        LOGGERS.get(logLevel()).log(logger(), error);
    }

    private Optional<RequestFacade> getRequestFacade() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return Optional.empty();
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        if (!(request instanceof RequestFacade)) {
            return Optional.empty();
        } else {
            return Optional.of((RequestFacade) request);
        }
    }

    private void addPath(ErrorData.ErrorDataBuilder errorBuilder, RequestFacade requestFacade) {
        errorBuilder.path(requestFacade.getRequestURI());
    }

    @SuppressWarnings("squid:S1148")
    private void addStackTrace(ErrorData.ErrorDataBuilder errorBuilder, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorBuilder.stackTrace(parseStackTraceToMap(stackTrace.toString()));
    }

    @SuppressWarnings("squid:S2095") // Resources should be close - false positive
    private Map<String, String> parseStackTraceToMap(String stackTrace) {
        String[] splittedStackTrace = stackTrace.replaceAll("\t", "  ").split("\\R");
        return IntStream.range(0, splittedStackTrace.length)
                .boxed()
                .collect(Collectors.toMap(i -> "[" + i + "]",
                        index -> splittedStackTrace[index],
                        (s, s2) -> null,
                        LinkedHashMap::new));
    }

    private boolean shouldIncludeStackTrace(ErrorProperties errorProperties,
                                            Optional<RequestFacade> requestFacadeOptional) {
        ErrorProperties.IncludeStacktrace include = errorProperties.getIncludeStacktrace();
        return include == ErrorProperties.IncludeStacktrace.ALWAYS ||
                (requestFacadeOptional.isPresent() &&
                        include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM &&
                        getTraceParameter(requestFacadeOptional.get()));
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return parameter != null && !"false".equalsIgnoreCase(parameter);
    }
}
