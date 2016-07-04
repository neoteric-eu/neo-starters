package com.neoteric.starter.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class StarterAopAutoConfiguration {

    @Bean
    ApiLoggingAspect apiLoggingAspect() {
        return new ApiLoggingAspect();
    }
}
