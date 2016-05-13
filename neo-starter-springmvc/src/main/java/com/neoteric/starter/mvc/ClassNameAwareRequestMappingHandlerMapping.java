package com.neoteric.starter.mvc;

import com.google.common.base.CaseFormat;
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
import java.util.Map;

@Setter
public class ClassNameAwareRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private CaseFormat caseFormat = CaseFormat.LOWER_HYPHEN;
    private Map<String, String> classSuffixToPrefix;

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }

            if (shouldAddClassNameContext(handlerType)) {
                RequestMappingInfo prefixMappingInfo = prefixMappingInfo(handlerType);
                info = prefixMappingInfo.combine(info);
            }
        }
        return info;
    }

    private String resolvePrefix(String initialPrefix) {
        if (!StringUtils.hasLength(initialPrefix)) {
            return initialPrefix;
        }
        String prefixToReturn = initialPrefix;

        if (!initialPrefix.startsWith("/")) {
            prefixToReturn = "/" + initialPrefix;
        }
        if (initialPrefix.endsWith("/")) {
            prefixToReturn = prefixToReturn.substring(0, prefixToReturn.length() - 1);
        }
        return prefixToReturn;
    }

    private boolean shouldAddClassNameContext(Class<?> handlerType) {
        if (classSuffixToPrefix == null || classSuffixToPrefix.isEmpty()) {
            return false;
        }


        String className = ClassUtils.getShortName(handlerType);
        return classSuffixToPrefix.keySet().stream().anyMatch(className::endsWith);
    }

    private RequestMappingInfo prefixMappingInfo(Class<?> handlerType) {
        String className = ClassUtils.getShortName(handlerType);
        StringBuilder completePrefix = new StringBuilder();
        Map.Entry<String, String> entry = classSuffixToPrefix.entrySet()
                .stream()
                .filter(e -> className.endsWith(e.getKey()))
                .findFirst()
                .get(); // I'm sure I will get entry as validation was done beforehand

        String prefix = resolvePrefix(entry.getValue());

        if (StringUtils.hasLength(prefix)) {
            completePrefix.append(prefix);
            completePrefix.append("/");
        }

        String classPath = className.substring(0, className.lastIndexOf(entry.getKey()));

        if (classPath.length() > 0) {
            completePrefix.append(resolveClassName(classPath));
        }

        return RequestMappingInfo
                .paths(completePrefix.toString())
                .build();
    }

    private String resolveClassName(String classPath) {
        String path = classPath.substring(classPath.lastIndexOf('.') + 1); // tackles inner static classes
        return CaseFormat.UPPER_CAMEL.to(caseFormat, path);
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = (element instanceof Class<?> ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }
}
