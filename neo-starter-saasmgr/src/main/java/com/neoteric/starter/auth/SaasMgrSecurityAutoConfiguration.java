package com.neoteric.starter.auth;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticationFilter;
import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@ComponentScan(basePackageClasses = SaasMgrAuthenticationProvider.class)
@ConditionalOnWebApplication
public class SaasMgrSecurityAutoConfiguration {

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        SaasMgrAuthenticationProvider saasMgrAuthenticationProvider;

        @Autowired
        AuthenticationManager authenticationManager;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(saasMgrAuthenticationProvider);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.
                    authorizeRequests().
                    anyRequest().authenticated();

            SaasMgrAuthenticationFilter filter = new SaasMgrAuthenticationFilter();
            filter.setAuthenticationManager(authenticationManager);
            http.addFilterBefore(filter, BasicAuthenticationFilter.class);
        }
    }
}
