package com.neoteric.starter.mvc.logging;

import com.google.common.collect.Lists;
import com.neoteric.starter.jackson.model.JsonApiList;
import com.neoteric.starter.jackson.model.JsonApiObject;
import com.neoteric.starter.mvc.ApiController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import static humanize.Humanize.capitalize;
import static humanize.Humanize.decamelize;

import java.util.List;
import java.util.StringJoiner;

@Aspect
@Slf4j
@AllArgsConstructor
public class ApiLoggingAspect {

    private static final int INITIAL_JOINER_LENGTH = 2;
    private final ApiLoggingProperties apiLoggingProperties;

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
        // Pointcut definition
    }

    @Around("publicMethod() && @within(apiController)")
    public Object around(ProceedingJoinPoint point, ApiController apiController) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();

        ApiLogger apiLogger = new ApiLogger(apiLoggingProperties, apiController.resourceName(),
                LoggerFactory.getLogger(point.getTarget().getClass()));

        MethodSignature signature = (MethodSignature) point.getSignature();

        Object[] args = point.getArgs();
        String[] parameterNames = signature.getParameterNames();
        Class[] parameterTypes = signature.getParameterTypes();
        String normalizedMethodName = capitalize(decamelize(signature.getName()));

        List<Integer> complexTypeIndexes = Lists.newArrayList();
        StringJoiner parametersJoiner = new StringJoiner(", ", "[", "]");
        if (parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].isPrimitive() || parameterTypes[i].getName().startsWith("java")) {
                    parametersJoiner.add(String.join(": ", parameterNames[i], String.valueOf(args[i])));
                } else {
                    complexTypeIndexes.add(i);
                }
            }
        }
        apiLogger.logEntryPoint(normalizedMethodName, evaluateParamJoiner(parametersJoiner));

        if (!complexTypeIndexes.isEmpty()) {
            StringJoiner complexParamsJoiner = new StringJoiner(", ", "[", "]");
            complexTypeIndexes.stream()
                    .forEach(index -> complexParamsJoiner.add(String.join(": ", parameterNames[index], String.valueOf(args[index]))));
            apiLogger.logCustomObjectDetails(evaluateParamJoiner(complexParamsJoiner));
        }

        try {
            return evaluateResponse(apiLogger, point.proceed());
        } finally {
            watch.stop();
            apiLogger.logExitPoint(normalizedMethodName, evaluateParamJoiner(parametersJoiner), watch.getTotalTimeSeconds());
        }
    }

    private Object evaluateResponse(ApiLogger apiLogger, Object proceed) {
        if (proceed instanceof JsonApiList) {
            JsonApiList<?> apiList = (JsonApiList) proceed;
            apiLogger.logReturnedJsonApiListSize(apiList.getData().size());
        } else if (proceed instanceof JsonApiObject) {
            JsonApiObject<?> apiObject = (JsonApiObject)proceed;
            apiLogger.logReturnedJsonApiObjectDetails(String.valueOf(apiObject.getData()));
        }
        return proceed;
    }

    private String evaluateParamJoiner(StringJoiner paramJoiner) {
        return paramJoiner.length() > INITIAL_JOINER_LENGTH ? paramJoiner.toString() : "";
    }
}