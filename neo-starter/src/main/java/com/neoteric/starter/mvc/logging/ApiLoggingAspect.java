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
import org.slf4j.Logger;
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

    private final ApiLoggingProperties apiLoggingProperties;

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && @within(apiController)")
    public Object around(ProceedingJoinPoint point, ApiController apiController) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();

        Logger log = LoggerFactory.getLogger(point.getTarget().getClass());
        ApiLogger apiLogger = new ApiLogger(apiLoggingProperties, apiController.resourceName(), log);

        MethodSignature signature = (MethodSignature) point.getSignature();

        Object[] args = point.getArgs();
        String resourceName = apiController.resourceName();
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
        apiLogger.logEntryPoint(normalizedMethodName, parametersJoiner.toString());

        if (complexTypeIndexes.size() > 0) {
            StringJoiner complexParamsJoiner = new StringJoiner(", ", "[", "]");
            complexTypeIndexes.stream().forEach(index -> {
                complexParamsJoiner.add(String.join(": ", parameterNames[index], String.valueOf(args[index])));
            });
            apiLogger.logCustomObjectDetails(complexParamsJoiner.toString());
        }

        try {
            Object proceed = point.proceed();
            if (proceed instanceof JsonApiList) {
                JsonApiList<?> list = (JsonApiList) proceed;
                list.getData().size();
                log.debug("{}Returning {} items.", resourceName, list.getData().size());
            } else if (proceed instanceof JsonApiObject) {
                log.info("{}Returning: [{}].", resourceName, String.valueOf(((JsonApiObject) proceed).getData()));
            }
            return proceed;
        } finally {
            watch.stop();

            apiLogger.logExitPoint(normalizedMethodName, parametersJoiner.toString(), watch.getTotalTimeSeconds());
            if (parametersJoiner.length() > 2) {
                log.info("{} {} took {} seconds.", normalizedMethodName, parametersJoiner.toString(), watch.getTotalTimeSeconds());
            } else {
                log.info("{} took {} seconds.", normalizedMethodName, watch.getTotalTimeSeconds());
            }
        }
    }

}


