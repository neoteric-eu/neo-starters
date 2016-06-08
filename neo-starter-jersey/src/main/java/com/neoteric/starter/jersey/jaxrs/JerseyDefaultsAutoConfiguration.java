package com.neoteric.starter.jersey.jaxrs;

import com.neoteric.starter.jersey.StarterConstants;
import com.neoteric.starter.jersey.jaxrs.validation.ValidationConfigurationProvider;
import com.neoteric.starter.jersey.jaxrs.endpoint.EndpointLoggingListener;
import com.neoteric.starter.jersey.jaxrs.time.ZonedDateTimeConverterProvider;
import com.neoteric.starter.jersey.exception.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import java.util.Map;


@Slf4j
@Configuration
@PropertySource("classpath:jersey-defaults.properties")
@AutoConfigureBefore(JerseyAutoConfiguration.class)
@EnableConfigurationProperties({JerseyProperties.class, NeoStarterJerseyProperties.class})
public class JerseyDefaultsAutoConfiguration {

    @Autowired
    private JerseyProperties jersey;

    @Autowired
    private ResourceConfig config;

    private String path;

    @Bean
    public ServletRegistrationBean jerseyServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(
                new ServletContainer(config), path);
        addInitParameters(registration, jersey);
        registration.setName(getServletRegistrationName(config));
        registration.setLoadOnStartup(1);
        return registration;
    }

    @PostConstruct
    public void path() {
        resolveApplicationPath();
    }

    private void resolveApplicationPath() {
        if (StringUtils.hasLength(this.jersey.getApplicationPath())) {
            this.path = parseApplicationPath(this.jersey.getApplicationPath());
        }
        else {
            this.path = findApplicationPath(AnnotationUtils
                    .findAnnotation(this.config.getClass(), ApplicationPath.class));
        }
    }

    private static String findApplicationPath(ApplicationPath annotation) {
        // Jersey doesn't like to be the default servlet, so map to /* as a fallback
        if (annotation == null) {
            return "/*";
        }
        return parseApplicationPath(annotation.value());
    }

    private static String parseApplicationPath(String applicationPath) {
        if (!applicationPath.startsWith("/")) {
            applicationPath = "/" + applicationPath;
        }
        return applicationPath.equals("/") ? "/*" : applicationPath + "/*";
    }

    private String getServletRegistrationName(ResourceConfig resourceConfig) {
        return ClassUtils.getUserClass(resourceConfig.getClass()).getName();
    }

    private void addInitParameters(RegistrationBean registration, JerseyProperties jerseyProperties) {
        for (Map.Entry<String, String> entry : jerseyProperties.getInit().entrySet()) {
            registration.addInitParameter(entry.getKey(), entry.getValue());
        }
    }

    @Configuration
    static class ResourceConfigDefaults {
        @Bean
        public ResourceConfigCustomizer configDefaults() {
            return config -> {
                config.register(MultiPartFeature.class);
                config.register(ZonedDateTimeConverterProvider.class);
                config.register(ResourceNotFoundExceptionMapper.class);
                config.register(ConstraintViolationExceptionMapper.class);
                config.register(NotFoundExceptionMapper.class);
                config.register(IllegalArgumentExceptionMapper.class);
                config.register(GlobalExceptionMapper.class);
                config.register(QueryParamExceptionMapper.class);
                config.register(ResourceConflictExceptionMapper.class);
                config.register(ValidationConfigurationProvider.class);
                config.property(ServerProperties.WADL_FEATURE_DISABLE, true);
            };
        }
    }

    @ConditionalOnProperty(prefix = "neostarter.swagger", name = "enabled", matchIfMissing = true)
    @Configuration
    static class ResourceConfigSwagger {

        @Bean
        public ResourceConfigCustomizer configSwagger() {
            return config -> config.packages(StarterConstants.SWAGGER_PACKAGE);
        }
    }

    @ConditionalOnProperty(prefix = "neostarter.jersey", name = "logEndpointsOnStartup", matchIfMissing = true)
    @Configuration
    static class ResourceConfigEndpointLogging {

        @Autowired
        private JerseyProperties jersey;

        @Bean
        public ResourceConfigCustomizer configEndpointLogging() {
            return config -> config.register(new EndpointLoggingListener(jersey.getApplicationPath()));
        }
    }

    @ConditionalOnClass({AccessDeniedException.class, AuthenticationException.class})
    @Configuration
    static class ResourceConfigAuthExceptionMapper {
        @Bean
        public ResourceConfigCustomizer configAuthExeptionMappers() {
            return config -> {
                config.register(AccessDeniedExceptionMapper.class);
                config.register(AuthenticationExceptionMapper.class);
            };
        }
    }

}