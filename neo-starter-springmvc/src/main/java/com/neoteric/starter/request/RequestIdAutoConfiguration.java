package com.neoteric.starter.request;

import com.neoteric.starter.mvc.StarterMvcProperties;
import com.neoteric.starter.request.tracing.RequestIdFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JerseyProperties.class)
public class RequestIdAutoConfiguration {

    @Autowired
    StarterMvcProperties starterMvcProperties;

    @Bean
    FilterRegistrationBean registerRequestIdFilter() {
        return  new FilterRegistrationBean(new RequestIdFilter(starterMvcProperties.getApi().getPath()));
    }

}