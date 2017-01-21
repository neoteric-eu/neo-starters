package eu.neoteric.starter.mvc;

import com.google.common.base.CaseFormat;
import eu.neoteric.starter.utils.PrefixResolver;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
public class ClassNameAwareRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private StarterMvcProperties.ApiProperties apiProps;

    public ClassNameAwareRequestMappingHandlerMapping(StarterMvcProperties.ApiProperties apiProps) {
        this.apiProps = apiProps;
    }

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


    private boolean shouldAddClassNameContext(Class<?> handlerType) {
        return handlerType.isAnnotationPresent(ApiController.class);
    }

    private RequestMappingInfo prefixMappingInfo(Class<?> handlerType) {
        StringBuilder completePrefix = new StringBuilder();

        String apiPath = apiProps.getPath();
        if (StringUtils.hasLength(apiPath)) {
            completePrefix.append(apiPath);
        }
        String prefix = PrefixResolver.resolve(retrievePrefix(handlerType));

        if (StringUtils.hasLength(prefix)) {
            completePrefix.append(prefix);
        }
        completePrefix.append("/");
        completePrefix.append(resolveClassName(ClassUtils.getShortName(handlerType)));

        return RequestMappingInfo
                .paths(completePrefix.toString())
                .build();
    }

    private String retrievePrefix(Class<?> handlerType) {
        ApiController annotation = handlerType.getAnnotation(ApiController.class);
        return StringUtils.isEmpty(annotation.prefix()) ? apiProps.getResources().getDefaultPrefix() : annotation.prefix();
    }

    private String resolveClassName(String className) {
        String resolvedClassName = className.substring(className.lastIndexOf('.') + 1); // tackles inner static classes
        String classNamePattern = apiProps.getResources().getClassNamePattern();
        if (classNamePattern != null) {
            String[] split = classNamePattern.split("\\?", 2);
            if (split.length > 1) {
            Pattern pattern = Pattern.compile("^" + split[0] + "([a-zA-Z0-9].*)" + split[1] + "$");
                Matcher matcher = pattern.matcher(resolvedClassName);
                if (matcher.find()) {
                    resolvedClassName = matcher.group(1);
                }
            }
        }

        return CaseFormat.UPPER_CAMEL.to(apiProps.getResources().getCaseFormat(), resolvedClassName);
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class<?> ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element);
        return requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null;
    }
}
