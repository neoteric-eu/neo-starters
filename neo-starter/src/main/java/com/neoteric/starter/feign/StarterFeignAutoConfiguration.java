package com.neoteric.starter.feign;

import com.neoteric.starter.feign.tracing.RequestIdAppendInterceptor;
import feign.Feign;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static com.neoteric.starter.StarterConstants.LOG_PREFIX;

@Slf4j
@Configuration
@ConditionalOnClass(Feign.class)
@PropertySource("classpath:hystrix-defaults.properties")
@EnableConfigurationProperties(CustomFeignProperties.class)
public class StarterFeignAutoConfiguration {

    @Autowired
    CustomFeignProperties feignProperties;

    @Bean
    public RequestIdAppendInterceptor idAppendInterceptor() {
        return new RequestIdAppendInterceptor();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        LOG.debug("{}Feign Logger level: {}", LOG_PREFIX, feignProperties.getLoggerLevel());
        return feignProperties.getLoggerLevel();
    }
}
