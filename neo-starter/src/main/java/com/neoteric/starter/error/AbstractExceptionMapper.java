package com.neoteric.starter.error;

import ch.qos.logback.classic.Level;
import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.Constants;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public abstract class AbstractExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    private static final String LOG_MESSAGE = "Exception mapped: ";

    protected abstract HttpStatus httpStatus();
    protected abstract Logger logger();
    protected abstract Level logLevel();
    protected abstract Object message(E throwable);
    protected Object errorCode(E throwable) {
        return null;
    }

    @FunctionalInterface
    interface LoggerFunction {
        void log(Logger logger, Throwable error);
    }

    ImmutableMap<Level, LoggerFunction> LOGGERS = ImmutableMap.<Level, LoggerFunction>builder()
            .put(Level.ERROR, (logger, error) -> logger.error(LOG_MESSAGE, error))
            .put(Level.WARN, (logger, error) -> logger.warn(LOG_MESSAGE, error))
            .put(Level.INFO, (logger, error) -> logger.info(LOG_MESSAGE, error))
            .put(Level.DEBUG, (logger, error) -> logger.debug(LOG_MESSAGE, error))
            .put(Level.TRACE, (logger, error) -> logger.trace(LOG_MESSAGE, error))
            .build();

    @Autowired
    ServerProperties serverProperties;

    @Override
    public Response toResponse(E error) {
        logError(error);
        Optional<RequestFacade> requestFacadeOptional = getRequestFacade();
        ErrorData.Builder errorBuilder = ErrorData
                .builder()
                .setTimestamp(ZonedDateTime.now())
                .setRequestId(String.valueOf(MDC.get(Constants.REQUEST_ID)))
                .setStatus(httpStatus().value())
                .setError(httpStatus().getReasonPhrase())
                .setMessage(message(error))
                .setErrorCode(errorCode(error))
                .setException(error.getClass().getName());

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

    private void addPath(ErrorData.Builder errorBuilder, RequestFacade requestFacade) {
        errorBuilder.setPath(requestFacade.getRequestURI());
    }

    private void addStackTrace(ErrorData.Builder errorBuilder, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorBuilder.setStackTrace(StringEscapeUtils.unescapeJson(stackTrace.toString()));
    }

    private boolean shouldIncludeStackTrace(ErrorProperties errorProperties,
                                              Optional<RequestFacade> requestFacadeOptional) {
        ErrorProperties.IncludeStacktrace include = errorProperties.getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        return requestFacadeOptional.isPresent() && include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM && getTraceParameter(requestFacadeOptional.get());
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return parameter != null && !"false".equals(parameter.toLowerCase());
    }
}
