package com.neoteric.starter.saasmgr;

import com.google.common.collect.Sets;
import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationProvider;
import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.filter.SaasMgrAuthenticationFilter;
import com.neoteric.starter.saasmgr.filter.SaasMgrAuthenticationMatcher;
import com.neoteric.starter.utils.PrefixResolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Set;

@Slf4j
@Configuration
@AutoConfigureBefore(SecurityAutoConfiguration.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
@Import({ SaasMgrSecurityAutoConfiguration.ApiSecurityConfiguration.class,
		SaasMgrSecurityAutoConfiguration.ManagementSecurityConfiguration.class })
public class SaasMgrSecurityAutoConfiguration {

	private final SaasMgrClient saasMgrClient;
	private final SecurityProperties securityProperties;

    @Bean
    SaasMgrAuthenticationProvider saasAuthenticationProvider() {
        return new SaasMgrAuthenticationProvider(saasMgrClient);
    }

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		SecurityProperties.User user = securityProperties.getUser();

		if (user.isDefaultPassword()) {
			LOG.info(
					String.format("%n%n{}Using default security password: %s%n",
							user.getPassword()), SaasMgrStarterConstants.LOG_PREFIX);
		}

		Set<String> roles = Sets.newLinkedHashSet(user.getRole());
		auth.authenticationProvider(saasAuthenticationProvider())
            .inMemoryAuthentication()
                .withUser(user.getName())
				.password(user.getPassword())
				.roles(roles.toArray(new String[roles.size()]));
	}

	@Configuration
	static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {


        @Value("#{environment.getProperty('neostarter.mvc.api.path') ?: environment.getProperty('neostarter.saasmgr.api.path') ?: ''}")
        String apiPath;

		@Bean
		SaasMgrAuthenticationMatcher saasMgrAuthenticationMatcher() {
			return new SaasMgrAuthenticationMatcher();
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			SaasMgrAuthenticationFilter filter = new SaasMgrAuthenticationFilter(
					saasMgrAuthenticationMatcher());
			http.antMatcher(PrefixResolver.resolve(apiPath) + "/**").addFilterBefore(
					filter, BasicAuthenticationFilter.class);
		}
	}

	@Configuration
	@Order(1)
	static class ManagementSecurityConfiguration extends
			WebSecurityConfigurerAdapter {


        @Value("#{environment.getProperty('neostarter.mvc.api.path') ?: environment.getProperty('neostarter.saasmgr.api.path') ?: ''}")
        String apiPath;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.requestMatcher(
					request -> {
						final String url = request.getServletPath()
								+ StringUtils.defaultString(request.getPathInfo());
						return !url.startsWith(PrefixResolver.resolve(apiPath) + "/");
					})
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic();
		}
	}
}
