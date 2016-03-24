package com.neoteric.starter.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.context.embedded.RegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import java.util.Map;


@Configuration
@PropertySource("classpath:jersey-defaults.properties")
@AutoConfigureBefore(JerseyAutoConfiguration.class)
@EnableConfigurationProperties(NeoStarterJerseyProperties.class)
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
}