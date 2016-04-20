package com.neoteric.starter.mvc.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.neoteric.starter.StarterConstants;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.web.servlet.HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;

public class RestExceptionResolver extends AbstractHandlerExceptionResolver implements InitializingBean, ApplicationContextAware {

    private static final String LOG_MESSAGE = "Exception mapped: ";

    private static final ImmutableMap<Level, LoggerFunction> LOGGERS = ImmutableMap.<Level, LoggerFunction>builder()
            .put(Level.ERROR, (logger, error) -> logger.error(LOG_MESSAGE, error))
            .put(Level.WARN, (logger, error) -> logger.warn(LOG_MESSAGE, error))
            .put(Level.INFO, (logger, error) -> logger.info(LOG_MESSAGE, error))
            .put(Level.DEBUG, (logger, error) -> logger.debug(LOG_MESSAGE, error))
            .put(Level.TRACE, (logger, error) -> logger.trace(LOG_MESSAGE, error))
            .build();

    @FunctionalInterface
    private interface LoggerFunction {
        void log(Logger logger, Throwable error);
    }

    @Autowired
    private Clock clock;

    @Autowired
    ServerProperties serverProperties;

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionResolver.class);

    private final ObjectMapper objectMapper;
    private final RestExceptionHandlerRegistry restExceptionHandlerRegistry;
    private HandlerMethodReturnValueHandler responseProcessor;
    private ApplicationContext applicationContext;
    private MethodParameter methodParameter;

    public RestExceptionResolver(ObjectMapper objectMapper, RestExceptionHandlerRegistry restExceptionHandlerRegistry) {
        this.objectMapper = objectMapper;
        this.restExceptionHandlerRegistry = restExceptionHandlerRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        responseProcessor = new HttpEntityMethodProcessor(Lists.newArrayList(new MappingJackson2HttpMessageConverter(objectMapper)));
        Method method = ClassUtils.getMethod(RestExceptionHandler.class, "handleException", Throwable.class, HttpServletRequest.class);
        methodParameter = new MethodParameter(method, -1);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex) {
        // See http://stackoverflow.com/a/12979543/2217862
        request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        ExceptionHandlerBinding binding = restExceptionHandlerRegistry.findBindingFor(ex.getClass())
                .orElseThrow(NoExceptionHandlerFoundException::new);
        RestExceptionHandler handler = (RestExceptionHandler) applicationContext.getBean(binding.getExceptionHandlerBeanName());

        LOGGERS.get(binding.getLogLevel()).log(binding.getLogger(), ex);
        ErrorData errorData = createErrorData(handler.handleException(ex, request), binding, request, ex);
        ResponseEntity<ErrorData> responseEntity = new ResponseEntity<>(errorData, new HttpHeaders(), binding.getHttpStatus());
        MethodParameter returnMethodParameter = new MethodParameter(methodParameter);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        try {
            responseProcessor.handleReturnValue(responseEntity, returnMethodParameter, mavContainer, new ServletWebRequest(request, response));
        } catch (Exception e) {
            LOG.error("ERROR", e);
            return null;
        }
        return new ModelAndView();
    }

    private ErrorData createErrorData(Object message, ExceptionHandlerBinding binding, HttpServletRequest request, Exception ex) {
        //Validate if object is of type, String, List, Map
        HttpStatus httpStatus = binding.getHttpStatus();
        ErrorData.ErrorDataBuilder builder = ErrorData.builder()
                .timestamp(ZonedDateTime.now(clock))
                .requestId(String.valueOf(MDC.get(StarterConstants.REQUEST_ID)))
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .exception(ex.getClass().getName());

        if (shouldIncludeStackTrace(serverProperties.getError(), request)) {
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

    private boolean shouldIncludeStackTrace(ErrorProperties errorProperties, HttpServletRequest request) {
        ErrorProperties.IncludeStacktrace include = errorProperties.getIncludeStacktrace();
        return include == ErrorProperties.IncludeStacktrace.ALWAYS ||
                (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM && getTraceParameter(request));
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return parameter != null && !"false".equalsIgnoreCase(parameter);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    private class NoExceptionHandlerFoundException extends RuntimeException {
    }
}
