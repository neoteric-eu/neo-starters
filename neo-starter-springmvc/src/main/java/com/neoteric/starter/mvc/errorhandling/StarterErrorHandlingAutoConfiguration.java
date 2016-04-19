package com.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.Sets;
import com.neoteric.starter.mvc.StarterMvcAutoConfiguration;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.springframework.aop.interceptor.ExposeBeanNameAdvisors.getBeanName;


@Slf4j
@Configuration
@AutoConfigureBefore(StarterMvcAutoConfiguration.class)
public class StarterErrorHandlingAutoConfiguration implements BeanClassLoaderAware {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Bean
    @ConditionalOnMissingBean(RestExceptionHandlerRegistry.class)
    RestExceptionHandlerRegistry restExceptionHandlerRegistry(BeanFactory beanFactory) throws ClassNotFoundException {

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(this.environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestExceptionHandlerProvider.class));

        Set<ExceptionHandlerBinding> bindings = Sets.newHashSet();

        for (String basePackage : getMappingBasePackages(beanFactory)) {
            if (StringUtils.hasText(basePackage)) {
                for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                    if (candidate instanceof AnnotatedBeanDefinition) {
                        // verify annotated class is an interface
                        AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidate;
                        AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                        Assert.isTrue(annotationMetadata.isConcrete(),
                                "@RestExceptionHandlerProvider can only be specified on a concrete class");

                        Map<String, Object> attributes = annotationMetadata
                                .getAnnotationAttributes(RestExceptionHandlerProvider.class.getCanonicalName());

                        Class<?> exceptionHandlerClass = ClassUtils.forName(candidate.getBeanClassName(), this.classLoader);
                        String handlerBeanName = getHandlerBeanName(exceptionHandlerClass);

                        bindings.add(ExceptionHandlerBinding.builder()
                                .exceptionClass(getExceptionClass(exceptionHandlerClass))
                                .httpStatus(getHttpStatus(attributes))
                                .logLevel(getLogLevel(attributes))
                                .logger(LoggerFactory.getLogger(exceptionHandlerClass))
                                .exceptionHandlerBeanName(getHandlerBeanName(handlerBeanName))
                                .build());

                        beanFactory.
                                String name = getClientName(attributes);
                        registerClientConfiguration(registry, name,
                                attributes.get("configuration"));

                        registerFeignClient(registry, annotationMetadata, attributes);
                    }

                    registry.addExceptionHandlerClass(ClassUtils.forName(candidate.getBeanClassName(), this.classLoader));
                }
            }
        }

        RestExceptionHandlerRegistry registry = new RestExceptionHandlerRegistry(exceptionHandlerBindings);
        registry.setApplicationContext(applicationContext);

        LOG.error("REGISTRY: {}", registry);
        return registry;
    }

    private String getHandlerBeanName(Class<?> exceptionHandlerClass) {
        String shortClassName = exceptionHandlerClass.getSimpleName();
        return Introspector.decapitalize(shortClassName);
    }

    private Level getLogLevel(Map<String, Object> attributes) {
        return (Level) attributes.get("logLevel");
    }

    private HttpStatus getHttpStatus(Map<String, Object> attributes) {
        return (HttpStatus) attributes.get("httpStatus");
    }

    private Class<? extends Throwable> getExceptionClass(Class<?> clazz) throws ClassNotFoundException {
        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == RestExceptionHandler.class) {
                //noinspection unchecked
                return (Class<? extends Throwable>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        //Won't get in here
        return null;
    }

    private static Collection<String> getMappingBasePackages(BeanFactory beanFactory) {
        try {
            return AutoConfigurationPackages.get(beanFactory);
        } catch (IllegalStateException ex) {
            LOG.error("XXXXX", ex);
            // no auto-configuration package registered yet
            return Collections.emptyList();
        }
    }
}
