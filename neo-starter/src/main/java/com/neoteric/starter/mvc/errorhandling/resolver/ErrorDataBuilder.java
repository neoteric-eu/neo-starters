package com.neoteric.starter.mvc.errorhandling.resolver;

import com.neoteric.starter.StarterConstants;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import org.apache.log4j.MDC;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.ResponseEntity.status;

public final class ErrorDataBuilder {

    private final Clock clock;
    private final ServerProperties serverProperties;

    public ErrorDataBuilder(Clock clock, ServerProperties serverProperties) {
        this.clock = clock;
        this.serverProperties = serverProperties;
    }

    public ErrorData build(RestExceptionHandler handler, ExceptionHandlerBinding binding,
                           HttpServletRequest request, Exception ex) {
        //Validate if object is of type, String, List, Map
        HttpStatus httpStatus = binding.getHttpStatus();
        ErrorData.ErrorDataBuilder builder = ErrorData.builder()
                .timestamp(ZonedDateTime.now(clock))
                .requestId(MDC.get(StarterConstants.REQUEST_ID_HEADER) == null ? null : MDC.get(StarterConstants.REQUEST_ID_HEADER).toString())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(handler.errorMessage(ex, request))
                .additionalInfo(handler.additionalInfo(ex, request))
                .path(request.getRequestURI());

        if (!binding.isSuppressException()) {
            builder.exception(ex.getClass().getName());
        }

        if (shouldIncludeStackTrace(binding, serverProperties.getError(), request)) {
            addStackTrace(builder, ex);
        }

        return builder.build();
    }

    @SuppressWarnings("squid:S1148")
    private void addStackTrace(ErrorData.ErrorDataBuilder errorBuilder, Exception ex) {
        StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));
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

    private boolean shouldIncludeStackTrace(ExceptionHandlerBinding binding, ErrorProperties errorProperties, HttpServletRequest request) {
        if (binding.isSuppressStacktrace()) {
            return false;
        }
        ErrorProperties.IncludeStacktrace include = errorProperties.getIncludeStacktrace();
        return include == ErrorProperties.IncludeStacktrace.ALWAYS ||
                (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM && getTraceParameter(request));
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return parameter != null && !"false".equalsIgnoreCase(parameter);
    }
}
