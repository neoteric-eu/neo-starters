package com.neoteric.starter.mvc.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.jackson.StarterJacksonBeforeAutoConfiguration;
import com.neoteric.starter.mvc.StarterMvcAutoConfiguration;
import com.neoteric.starter.mvc.StarterMvcProperties;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import com.neoteric.starter.mvc.errorhandling.resolver.ErrorDataBuilder;
import com.neoteric.starter.mvc.errorhandling.resolver.RestExceptionResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;


@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "neostarter.mvc.restErrorHandling", value = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(StarterMvcAutoConfiguration.class)
@AutoConfigureAfter(StarterJacksonBeforeAutoConfiguration.class)
public class StarterErrorHandlingAutoConfiguration {

    private final ApplicationContext applicationContext;
    private final Clock clock;
    private final ServerProperties serverProperties;
    private final StarterMvcProperties starterMvcProperties;

    public StarterErrorHandlingAutoConfiguration(ApplicationContext applicationContext,
                                                 Clock clock,
                                                 ServerProperties serverProperties,
                                                 StarterMvcProperties starterMvcProperties) {
        this.applicationContext = applicationContext;
        this.clock = clock;
        this.serverProperties = serverProperties;
        this.starterMvcProperties = starterMvcProperties;
    }

    @Bean
    ErrorDataBuilder errorDataBuilder() {
        return new ErrorDataBuilder(clock, serverProperties, starterMvcProperties.getErrorHandling().getCauseMapping());
    }

    @Bean
    RestExceptionResolver restExceptionResolver(ObjectMapper objectMapper, RestExceptionHandlerRegistry restExceptionHandlerRegistry) {
        RestExceptionResolver restExceptionResolver = new RestExceptionResolver(objectMapper, errorDataBuilder(), restExceptionHandlerRegistry);
        restExceptionResolver.setApplicationContext(this.applicationContext);
        return restExceptionResolver;
    }
}
