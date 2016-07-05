package com.neoteric.starter.mvc.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ConditionalOnProperty(prefix = "neostarter.mvc.logging", name = "enabled", havingValue = "true")
@EnableAspectJAutoProxy
@EnableConfigurationProperties(ApiLoggingProperties.class)
public class ApiLoggingAutoConfiguration {

    @Bean
    ApiLoggingAspect apiLoggingAspect(ApiLoggingProperties apiLoggingProperties) {
        return new ApiLoggingAspect(apiLoggingProperties);
    }
}
