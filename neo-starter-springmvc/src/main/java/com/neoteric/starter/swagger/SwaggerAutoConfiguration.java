package com.neoteric.starter.swagger;

import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Swagger.class)
@ConditionalOnProperty(value = "neostarter.swagger.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);

    @Autowired
    SwaggerProperties swaggerProperties;

}
