package eu.neoteric.starter.mvc.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(ApiLoggingProperties.class)
@ConditionalOnProperty(prefix = "neostarter.mvc.logging", name = "enabled",  havingValue = "true", matchIfMissing = true)
@EnableAspectJAutoProxy
public class ApiLoggingAutoConfiguration {

    @Bean
    ApiLoggingAspect apiLoggingAspect(ApiLoggingProperties apiLoggingProperties) {
        return new ApiLoggingAspect(apiLoggingProperties);
    }
}
