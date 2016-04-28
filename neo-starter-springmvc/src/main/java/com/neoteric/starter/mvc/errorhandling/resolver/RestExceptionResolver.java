package com.neoteric.starter.mvc.errorhandling.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
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
import java.lang.reflect.Method;

import static org.springframework.web.servlet.HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;

public class RestExceptionResolver extends AbstractHandlerExceptionResolver implements InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionResolver.class);

    private final ObjectMapper objectMapper;
    private final ErrorDataBuilder errorDataBuilder;
    private final ErrorLogger errorLogger;
    private final RestExceptionHandlerRegistry restExceptionHandlerRegistry;

    private HandlerMethodReturnValueHandler responseProcessor;
    private ApplicationContext applicationContext;
    private MethodParameter methodParameter;

    public RestExceptionResolver(ObjectMapper objectMapper, ErrorDataBuilder errorDataBuilder,
                                 RestExceptionHandlerRegistry restExceptionHandlerRegistry) {
        this.objectMapper = objectMapper;
        this.errorDataBuilder = errorDataBuilder;
        this.restExceptionHandlerRegistry = restExceptionHandlerRegistry;
        this.errorLogger = new ErrorLogger();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        responseProcessor = new HttpEntityMethodProcessor(Lists.newArrayList(new MappingJackson2HttpMessageConverter(objectMapper)));
        Method method = ClassUtils.getMethod(RestExceptionHandler.class, "errorMessage", Exception.class, HttpServletRequest.class);
        methodParameter = new MethodParameter(method, -1);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex) {
        // See http://stackoverflow.com/a/12979543/2217862
        request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        ExceptionHandlerBinding binding = restExceptionHandlerRegistry.findBindingFor(ex.getClass())
                .orElseThrow(NoExceptionHandlerFoundException::new);
        RestExceptionHandler<?> handler = applicationContext.getBean(binding.getExceptionHandlerBeanName(), RestExceptionHandler.class);

        errorLogger.log(binding, ex);
        ErrorData errorData = errorDataBuilder.build(handler, binding, request, ex);
        return processResponse(errorData, binding, new ServletWebRequest(request, response));
    }

    private ModelAndView processResponse(ErrorData errorData, ExceptionHandlerBinding binding, ServletWebRequest servletWebRequest) {
        ResponseEntity<ErrorData> responseEntity = new ResponseEntity<>(errorData, new HttpHeaders(), binding.getHttpStatus());
        MethodParameter returnMethodParameter = new MethodParameter(methodParameter);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        try {
            responseProcessor.handleReturnValue(responseEntity, returnMethodParameter, mavContainer, servletWebRequest);
        } catch (Exception e) {
            LOG.error("ERROR", e);
            return null;
        }
        return new ModelAndView();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public static class NoExceptionHandlerFoundException extends RuntimeException {
    }

}
