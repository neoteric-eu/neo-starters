package com.neoteric.starter.auth;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticationProvider;
import com.neoteric.starter.auth.saasmgr.SaasMgrConnector;
import com.neoteric.starter.auth.saasmgr.filter.SaasMgrAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties
public class SaasMgrSecurityAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SaasMgrSecurityAutoConfiguration.class);

    @Bean
    SaasMgrConnector saasMgrConnector() {
        return new SaasMgrConnector();
    }

    @Bean
    SaasMgrAuthenticationProvider saasMgrAuthenticationProvider() {
        return new SaasMgrAuthenticationProvider(saasMgrConnector());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(saasMgrAuthenticationProvider());
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    @EnableFeignClients
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        JerseyProperties jerseyProperties;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SaasMgrAuthenticationFilter filter = new SaasMgrAuthenticationFilter(jerseyProperties.getApplicationPath());
            http.addFilterBefore(filter, BasicAuthenticationFilter.class);
        }
    }
}
