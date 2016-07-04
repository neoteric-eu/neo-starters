package com.neoteric.starter.aop;

import com.google.common.collect.Lists;
import com.neoteric.starter.jackson.model.JsonApiList;
import com.neoteric.starter.mvc.ApiController;
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

@Aspect
@Slf4j
public class ApiLoggingAspect {

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Around("publicMethod() && @within(apiController)")
    public Object around(ProceedingJoinPoint point, ApiController apiController) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();

        String resourceName = apiController.resourceName();
        Logger log = LoggerFactory.getLogger(point.getTarget().getClass());
        MethodSignature signature = (MethodSignature)point.getSignature();

        Object[] args = point.getArgs();
        String[] parameterNames = signature.getParameterNames();
        Class[] parameterTypes = signature.getParameterTypes();



        String normalized = capitalize(decamelize(signature.getName()));
        StringBuilder infoLog = new StringBuilder(normalized);

        List<Integer> complexTypeIndexes = Lists.newArrayList();
        if (parameterTypes.length > 0) {
            infoLog.append(" [");
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].isPrimitive() || parameterNames[i].getClass().getName().startsWith("java")) {
                    infoLog.append(parameterNames[i]);
                    infoLog.append(": ");
                    infoLog.append(args[i]);
                } else {
                    complexTypeIndexes.add(i);
                }
            }
            infoLog.append("]");
        }

        log.info("{}.",infoLog.toString());
        try {
            Object proceed = point.proceed();
            if (proceed instanceof JsonApiList) {
                JsonApiList<?> list = (JsonApiList)proceed;
                list.getData().size();
            }
            return proceed;
        } finally {
        watch.stop();
            log.info("{} took {} seconds.", infoLog, watch.getTotalTimeSeconds());

        }
    }

}
