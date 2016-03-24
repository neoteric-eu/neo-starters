package com.neoteric.starter.jersey;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:jersey-defaults.properties")
@AutoConfigureBefore(JerseyAutoConfiguration.class)
@EnableConfigurationProperties(NeoStarterJerseyProperties.class)
public class JerseyDefaultsAutoConfiguration {

//    @Bean
//    public ServletRegistrationBean jerseyServletRegistration(JerseyProperties jerseyProperties, ResourceConfig resourceConfig) {
//        ServletRegistrationBean registration = new ServletRegistrationBean(
//                new ServletContainer(resourceConfig), jerseyProperties.getApplicationPath());
//        addInitParameters(registration, jerseyProperties);
//        registration.setName(resourceConfig.getClass().getName());
//        registration.setLoadOnStartup(1);
//        return registration;
//    }
//
//    private void addInitParameters(RegistrationBean registration, JerseyProperties jerseyProperties) {
//        for (Map.Entry<String, String> entry : jerseyProperties.getInit().entrySet()) {
//            registration.addInitParameter(entry.getKey(), entry.getValue());
//        }
//    }
}