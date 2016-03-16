package com.neoteric.starter.test.tomcat;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Forces to use Tomcat embedded container to use in Integration Tests. Due to Wiremock and its transitive Jetty
 * dependency, the latter was used.
 */
@Configuration
@AutoConfigureBefore(EmbeddedServletContainerAutoConfiguration.class)
public class ForceTomcatAutoConfiguration {

    @Bean
    TomcatEmbeddedServletContainerFactory tomcat() {
        return new TomcatEmbeddedServletContainerFactory();
    }
}