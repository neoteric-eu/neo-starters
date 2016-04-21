package com.neoteric.starter.mvc.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.neoteric.starter.jackson.StarterJacksonAutoConfiguration;
import com.neoteric.starter.mvc.StarterMvcAutoConfiguration;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import com.neoteric.starter.mvc.errorhandling.mapper.common.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
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
import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Configuration
@AutoConfigureBefore(StarterMvcAutoConfiguration.class)
@AutoConfigureAfter(StarterJacksonAutoConfiguration.class)
public class StarterErrorHandlingAutoConfiguration implements BeanClassLoaderAware {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private Clock clock;

    @Autowired
    private ServerProperties serverProperties;

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Bean
    Set<ExceptionHandlerBinding> scannedBindings(BeanFactory beanFactory) {
        Set<ExceptionHandlerBinding> bindings = Sets.newHashSet();
        String basePackage = getMappingBasePackage(beanFactory);
        if (StringUtils.isEmpty(basePackage)) {
            return bindings;
        }

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(this.environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestExceptionHandlerProvider.class));

        Set<ExceptionHandlerBinding> scannedBindings = scanner.findCandidateComponents(basePackage).stream()
                .filter(beanDefinition -> AnnotatedBeanDefinition.class.isAssignableFrom(beanDefinition.getClass()))
                .map(beanDefinition -> {
                    AnnotatedBeanDefinition annotatedBeanDef = (AnnotatedBeanDefinition) beanDefinition;
                    AnnotationMetadata annotationMetadata = annotatedBeanDef.getMetadata();
                    Assert.isTrue(annotationMetadata.isConcrete(),
                            "@RestExceptionHandlerProvider can only be specified on a concrete class");
                    Class<?> exceptionHandlerClass = getExceptionClass(annotatedBeanDef);
                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(RestExceptionHandlerProvider.class.getCanonicalName());

                    return ExceptionHandlerBinding.builder()
                            .logger(LoggerFactory.getLogger(exceptionHandlerClass))
                            .exceptionClass(getExceptionClass(exceptionHandlerClass))
                            .exceptionHandlerBeanName(getHandlerBeanName(exceptionHandlerClass))
                            .httpStatus((HttpStatus) attributes.get("httpStatus"))
                            .logLevel((Level) attributes.get("logLevel"))
                            .suppressStacktrace((boolean) attributes.get("suppressStacktrace"))
                            .suppressException((boolean) attributes.get("suppressException"))
                            .build();
                }).collect(Collectors.toSet());

        bindings.addAll(scannedBindings);
        return bindings;
    }

    @Bean
    Set<ExceptionHandlerBinding> defaultBindings() {
        Set<ExceptionHandlerBinding> bindings = Sets.newHashSet();
        bindings.add(ExceptionHandlerBinding.builder()
                .exceptionClass(Exception.class)
                .exceptionHandlerBeanName("globalExceptionHandler")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .logLevel(Level.ERROR)
                .suppressException(false)
                .suppressStacktrace(false)
                .logger(LoggerFactory.getLogger(GlobalExceptionHandler.class))
                .build());
    }

    @Bean
    @ConditionalOnMissingBean(RestExceptionHandlerRegistry.class)
    RestExceptionHandlerRegistry restExceptionHandlerRegistry(BeanFactory beanFactory) throws ClassNotFoundException {


//            BeanDefinitionRegistry beanFactoryRegistry = (BeanDefinitionRegistry) beanFactory;
//            BeanDefinition definition = getExceptionHandlerBeanDefinition(exceptionHandlerClass);
//            beanFactoryRegistry.registerBeanDefinition(handlerBeanName, definition);
//            LOG.info("REGISTERING: {}", handlerBeanName);

        RestExceptionHandlerRegistry registry = new RestExceptionHandlerRegistry(bindings);
        return registry;
    }

    private Class<?> getExceptionClass(AnnotatedBeanDefinition beanDefinition) {
        try {
            return ClassUtils.forName(beanDefinition.getBeanClassName(), this.classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static BeanDefinition getExceptionHandlerBeanDefinition(Class<?> exceptionHandlerClass) {
        return BeanDefinitionBuilder
                .genericBeanDefinition(exceptionHandlerClass)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT)
                .getBeanDefinition();
    }

    @Bean
    ErrorDataBuilder errorDataBuilder() {
        return new ErrorDataBuilder(clock, serverProperties);
    }

    @Bean
    RestExceptionResolver restExceptionResolver(ObjectMapper objectMapper, RestExceptionHandlerRegistry restExceptionHandlerRegistry) {
        RestExceptionResolver restExceptionResolver = new RestExceptionResolver(objectMapper, errorDataBuilder(), restExceptionHandlerRegistry);
        restExceptionResolver.setApplicationContext(this.applicationContext);
        return restExceptionResolver;
    }

    private String getHandlerBeanName(Class<?> exceptionHandlerClass) {
        String shortClassName = exceptionHandlerClass.getSimpleName();
        return Introspector.decapitalize(shortClassName);
    }

    private Class<? extends Throwable> getExceptionClass(Class<?> clazz) {
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

    private static String getMappingBasePackage(BeanFactory beanFactory) {
        try {
            return AutoConfigurationPackages.get(beanFactory).get(0);
        } catch (IllegalStateException ex) {
            LOG.warn("AutoConfiguration is not enabled. Exception handlers scanning is off.");
            return "";
        }
    }
}
