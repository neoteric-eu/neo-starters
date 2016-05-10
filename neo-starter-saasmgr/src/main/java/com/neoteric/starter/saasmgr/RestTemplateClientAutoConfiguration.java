package com.neoteric.starter.saasmgr;

import com.neoteric.starter.saasmgr.client.RestTemplateSaasMgrClient;
import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import feign.Feign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@AutoConfigureBefore(SaasMgrAutoConfiguration.class)
@ConditionalOnMissingBean(SaasMgrClient.class)
public class RestTemplateClientAutoConfiguration {

    @Bean
    RestTemplate saasMgrRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    SaasMgrClient restTemplateSaasMgrClient() {
        LOG.debug("{}Using restTemplateSaasMgrClient", SaasMgrStarterConstants.LOG_PREFIX);
        return new RestTemplateSaasMgrClient(saasMgrRestTemplate());
    }
}
