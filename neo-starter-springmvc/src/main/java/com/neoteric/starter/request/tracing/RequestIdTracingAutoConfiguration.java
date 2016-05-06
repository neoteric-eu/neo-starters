package com.neoteric.starter.request.tracing;

import com.google.common.collect.Lists;
import com.neoteric.starter.mvc.StarterMvcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;

@Configuration
@EnableConfigurationProperties(StarterMvcProperties.class)
public class RequestIdTracingAutoConfiguration {

    @Autowired
    StarterMvcProperties starterMvcProperties;

    @Autowired(required = false)
    List<RequestIdListener> requestIdListeners = Lists.newArrayList();

    @Bean
    MDCHystrixConcurrencyStrategy requestIdHystrixConcurrencyStrategy() {
        return new MDCHystrixConcurrencyStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(RequestIdGenerator.class)
    RequestIdGenerator requestIdGenerator() {
        return new UuidRequestIdGenerator();
    }

    @Bean
    FilterRegistrationBean registerRequestIdFilter(RequestIdGenerator generator) {
        RequestIdFilter filter = new RequestIdFilter(generator, requestIdListeners, starterMvcProperties.getApi().getPath());
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

}