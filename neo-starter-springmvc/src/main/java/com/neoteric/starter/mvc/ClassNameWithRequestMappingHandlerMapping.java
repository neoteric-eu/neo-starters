package com.neoteric.starter.mvc;

import lombok.Setter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Setter
public class ClassNameWithRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private String initialPrefix;
    private String classSuffix;
    private CaseFormatMode caseFormatMode;

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {

            if (shouldAddClassNameContext(handlerType)) {
                RequestMappingInfo prefixMappingInfo = prefixMappingInfo(handlerType);
                                                          info = prefixMappingInfo.combine(info);
            }

            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    public void setInitialPrefix(String initialPrefix) {
        if (StringUtils.hasLength(initialPrefix)) {
            if (!initialPrefix.startsWith("/")) {
                this.initialPrefix = "/" + initialPrefix;
            }
            if (initialPrefix.endsWith("/")) {
                this.initialPrefix = this.initialPrefix.substring(0, initialPrefix.length() - 1);
            }
        }
    }

    private boolean shouldAddClassNameContext(Class<?> handlerType) {
        if (StringUtils.isEmpty(classSuffix)) {
            return false;
        }
        String className = ClassUtils.getShortName(handlerType);
        return className.endsWith(classSuffix);
    }

    private RequestMappingInfo prefixMappingInfo(Class<?> handlerType) {

        StringBuilder completePrefix = new StringBuilder();

        if (StringUtils.hasLength(initialPrefix)) {
            completePrefix.append(initialPrefix);
            completePrefix.append("/");
        }

        String className = ClassUtils.getShortName(handlerType);
        String classPath = className.substring(0, className.lastIndexOf(classSuffix));

        if (classPath.length() > 0) {
            completePrefix.append(classPath.toLowerCase());
        }

        return RequestMappingInfo
                .paths(completePrefix.toString())
                .build();
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = (element instanceof Class<?> ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }

}
