package com.neoteric.starter.jersey.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.jersey.request.params.RequestParametersFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JerseyProperties.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class RequestParamsAutoConfiguration {

    @Autowired
    JerseyProperties jerseyProperties;

    @Bean
    FilterRegistrationBean registerRequestParamsFilter(ObjectMapper objectMapper) throws Exception {
        return new FilterRegistrationBean(new RequestParametersFilter(objectMapper, jerseyProperties.getApplicationPath()));
    }
}
