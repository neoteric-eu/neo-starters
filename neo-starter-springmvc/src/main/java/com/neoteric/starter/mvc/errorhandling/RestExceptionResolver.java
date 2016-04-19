package com.neoteric.starter.mvc.errorhandling;

import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.servlet.HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;

public class RestExceptionResolver extends AbstractHandlerExceptionResolver implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // Don't pull default filters (@Component, etc.):
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        return provider;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex) {

        // See http://stackoverflow.com/a/12979543/2217862
        request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        RestExceptionHandler<Throwable> handler = resolveExceptionHandler(ex.getClass());
        return null;
    }

    protected RestExceptionHandler<Throwable> resolveExceptionHandler(Class<? extends Throwable> exceptionClass) {
        for (Class clazz = exceptionClass; clazz != Throwable.class; clazz = clazz.getSuperclass()) {
//            if (handlers.containsKey(clazz)) {
//                return handlers.get(clazz);
//            }
        }
        throw new NoExceptionHandlerFoundException();
    }



    private class NoExceptionHandlerFoundException extends RuntimeException {
    }
}
