package com.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.Sets;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@AutoConfigureBefore(StarterErrorHandlingAutoConfiguration.class)
@AutoConfigureAfter(ExceptionHandlersRegistryAutoConfiguration.class)
@ConditionalOnProperty(prefix = "neostarter.mvc.restErrorHandling", value = "enabled", havingValue = "true", matchIfMissing = true)
@Import(ScannedExceptionHandlersAutoConfiguration.ScannedExceptionHandlersRegistrar.class)
@SuppressWarnings("squid:S1118")
public class ScannedExceptionHandlersAutoConfiguration {

    @Slf4j
    static class ScannedExceptionHandlersRegistrar extends AbstractExceptionHandlerRegistrar
            implements ResourceLoaderAware, BeanClassLoaderAware {

        private ClassLoader classLoader;
        private ResourceLoader resourceLoader;

        @Override
        protected Set<Class<? extends RestExceptionHandler<? extends Exception>>> exceptionHandlerClasses(BeanDefinitionRegistry registry) {
            String basePackage = getMappingBasePackage((BeanFactory)registry);
            if (StringUtils.isEmpty(basePackage)) {
                return Sets.newHashSet();
            }

            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.setResourceLoader(this.resourceLoader);
            scanner.addIncludeFilter(new AnnotationTypeFilter(RestExceptionHandlerProvider.class));

            return scanner.findCandidateComponents(basePackage).stream()
                    .filter(beanDefinition -> AnnotatedBeanDefinition.class.isAssignableFrom(beanDefinition.getClass()))
                    .map(beanDefinition -> {
                        AnnotatedBeanDefinition annotatedBeanDef = (AnnotatedBeanDefinition) beanDefinition;
                        AnnotationMetadata annotationMetadata = annotatedBeanDef.getMetadata();
                        Assert.isTrue(annotationMetadata.isConcrete(),
                                "@RestExceptionHandlerProvider can only be specified on a concrete class");
                        return getExceptionClass(annotatedBeanDef);
                    }).collect(Collectors.toSet());
        }

        private static String getMappingBasePackage(BeanFactory beanFactory) {
            try {
                return AutoConfigurationPackages.get(beanFactory).get(0);
            } catch (IllegalStateException ex) {
                LOG.warn("AutoConfiguration is not enabled. Exception handlers scanning is off.", ex);
                return "";
            }
        }

        @SuppressWarnings("squid:S00112")
        private Class<? extends RestExceptionHandler<? extends Exception>> getExceptionClass(AnnotatedBeanDefinition beanDefinition) {
            try {
                return (Class<? extends RestExceptionHandler<?extends Exception>>)
                        ClassUtils.forName(beanDefinition.getBeanClassName(), this.classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e); // log ?
            }
        }

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }
}
