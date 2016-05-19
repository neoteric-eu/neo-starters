package com.neoteric.starter.saasmgr;

import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.client.feign.FeignSaasMgrClient;
import feign.Feign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AutoConfigureBefore(RestTemplateClientAutoConfiguration.class)
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(prefix ="neostarter.saasmgr.feign", name = "enabled", matchIfMissing = true)
@EnableFeignClients
public class FeignClientAutoConfiguration {

    @Bean
    @ConditionalOnClass(Feign.class)
    SaasMgrClient feignSaasMgrClient() {
        LOG.debug("{}Feign found on classpath - using FeignSaasMgrClient", SaasMgrStarterConstants.LOG_PREFIX);
        return new FeignSaasMgrClient();
    }
}
