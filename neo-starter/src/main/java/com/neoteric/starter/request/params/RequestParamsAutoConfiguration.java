package com.neoteric.starter.request.params;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.mvc.StarterMvcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StarterMvcProperties.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class RequestParamsAutoConfiguration {

    @Autowired
    StarterMvcProperties starterMvcProperties;

    @Bean
    FilterRegistrationBean registerRequestParamsFilter(ObjectMapper objectMapper) throws Exception {
        return new FilterRegistrationBean(new RequestParametersFilter(objectMapper, starterMvcProperties.getApi().getPath()));
    }
}
